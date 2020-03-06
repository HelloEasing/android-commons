package com.easing.commons.android.service;

import android.annotation.TargetApi;
import android.app.assist.AssistStructure;
import android.os.CancellationSignal;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import com.easing.commons.android.R;
import com.easing.commons.android.app.CommonApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

//此填充服务以name作为主键，通过此字段来去重，也通过此字段来作为显示时的表单标题
//当应用没有name字段时，也可以设置一个隐藏的自动填充控件，来设置自己想要的主键和表单标题
//当用户保存表单时，如果确实没有name字段，服务将会根据当前时间来生成一个name主键
//此填充服务不支持同一个控件拥有多个字段，当同一控件拥有多个字段时，将以第一个字段为准
//此填充服务不支持多个控件使用同一个字段，当多个控件使用同一字段时，只有最后的控件内容会被保存
@TargetApi(26)
public class AutofillService extends android.service.autofill.AutofillService {

    public static final String HINT_TYPE_NAME = "name";
    public static final String HINT_TYPE_PASSWORD = "password";
    public static final String HINT_TYPE_PHONE = "phone";
    public static final String HINT_TYPE_PACKAGE = "package";

    public static final List<String> HINT_TYPE_COLLECTIONS = new ArrayList();

    static {
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_NAME);
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_PASSWORD);
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_PHONE);
        HINT_TYPE_COLLECTIONS.add(HINT_TYPE_PACKAGE);
    }


    //模拟数据库中的数据，实际应用中，应该将这个数据保存在数据库中
    private static final List<Map<String, String>> suggestions = new ArrayList();

    static {
        //模拟数据库中的数据，实际应用中，应当把表单保存到数据库中，填充时再从数据库读取
        Map<String, String> suggestion1 = new HashMap();
        suggestion1.put(HINT_TYPE_NAME, "表单1");
        suggestion1.put(HINT_TYPE_PASSWORD, "123456");
        suggestion1.put(HINT_TYPE_PHONE, "18420015500");
        suggestions.add(suggestion1);
        Map<String, String> suggestion2 = new HashMap();
        suggestion2.put(HINT_TYPE_NAME, "表单2");
        suggestion2.put(HINT_TYPE_PASSWORD, "abcdefg");
        suggestion2.put(HINT_TYPE_PHONE, "17935842251");
        suggestions.add(suggestion2);
    }

    @Override
    public void onSaveRequest(SaveRequest request, SaveCallback callback) {
        //获取所有自动填充的节点
        List<AssistStructure> structures = request.getFillContexts().stream().map(FillContext::getStructure).collect(toList());
        List<AssistStructure.ViewNode> viewNodes = new ArrayList();
        parseAllAutofillNode(structures, viewNodes);
        //保存表单内容
        //每条建议记录对应一个Map，Map每个键值対代表控件的HintType和文本值
        Map<String, String> suggestion = new HashMap();
        for (AssistStructure.ViewNode viewNode : viewNodes) {
            String hintType = viewNode.getAutofillHints()[0];
            String value = viewNode.getText().toString();
            suggestion.put(hintType, value);
        }
        suggestions.add(suggestion);
        //成功
        callback.onSuccess();
    }

    @Override
    public void onFillRequest(FillRequest request, CancellationSignal cancellationSignal, FillCallback callback) {
        //获取所有自动填充节点的AutofillId和HintType
        List<AssistStructure> structures = request.getFillContexts().stream().map(FillContext::getStructure).collect(toList());
        Map<AutofillId, String> hintTypeMap = parseAllHintType(structures);
        //每条建议记录对应填充服务中的一个Dataset对象
        //每个Dataset代表了一套数据，包含name，password，phone等所有保存的字段
        //我们用Map来记录Dataset的数据，从而可以方便得将其存储到数据库或内存中
        FillResponse.Builder fillResponseBuilder = new FillResponse.Builder();
        for (Map<String, String> suggestion : suggestions) {
            Dataset.Builder datasetBuilder = new Dataset.Builder();
            String name = suggestion.get(HINT_TYPE_NAME);
            RemoteViews presentation = createPresentation(name);
            for (AutofillId autofillId : hintTypeMap.keySet()) {
                //将suggestion中的单个字段加入dataset
                String hintType = hintTypeMap.get(autofillId);
                String value = suggestion.get(hintType);
                if (value != null)
                    datasetBuilder.setValue(autofillId, AutofillValue.forText(value), presentation);
                //设置需要保存的表单节点，这一步一定要有，否则Activity退出时不会保存表单
                SaveInfo.Builder saveInfoBuilder = new SaveInfo.Builder(HINT_TYPE_COLLECTIONS.indexOf(hintType), new AutofillId[]{autofillId});
                //设置关联的节点，如果不设置，只有所有节点值发生变化时，系统才认为表单发生了变更，才会询问是否要保存表单
                saveInfoBuilder.setOptionalIds(hintTypeMap.keySet().stream().toArray(AutofillId[]::new));
                SaveInfo saveInfo = saveInfoBuilder.build();
                fillResponseBuilder.setSaveInfo(saveInfo);
            }
            Dataset dataset = datasetBuilder.build();
            fillResponseBuilder.addDataset(dataset);
        }
        FillResponse fillResponse = fillResponseBuilder.build();
        //成功
        callback.onSuccess(fillResponse);
    }

    //获取所有自动填充的节点
    private void parseAllAutofillNode(List<AssistStructure> structures, List<AssistStructure.ViewNode> autofillNodes) {
        for (AssistStructure structure : structures) {
            int windowNodeCount = structure.getWindowNodeCount();
            for (int i = 0; i < windowNodeCount; i++) {
                AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
                AssistStructure.ViewNode rootViewNode = windowNode.getRootViewNode();
                parseAllAutofillNode(rootViewNode, autofillNodes);
            }
        }
    }

    //获取所有自动填充的节点
    private void parseAllAutofillNode(AssistStructure.ViewNode viewNode, List<AssistStructure.ViewNode> autofillNodes) {
        if (viewNode.getAutofillHints() != null)
            autofillNodes.add(viewNode);
        int childCount = viewNode.getChildCount();
        for (int i = 0; i < childCount; i++)
            parseAllAutofillNode(viewNode.getChildAt(i), autofillNodes);
    }

    //获取所有Autofill节点的HintType
    private Map<AutofillId, String> parseAllHintType(List<AssistStructure> structures) {
        Map<AutofillId, String> hintTypeMap = new HashMap();
        for (AssistStructure structure : structures) {
            int windowNodeCount = structure.getWindowNodeCount();
            for (int i = 0; i < windowNodeCount; i++) {
                AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
                AssistStructure.ViewNode rootViewNode = windowNode.getRootViewNode();
                parseAllHintType(rootViewNode, hintTypeMap);
            }
        }
        return hintTypeMap;
    }

    //获取所有Autofill节点的HintType
    private void parseAllHintType(AssistStructure.ViewNode viewNode, Map<AutofillId, String> hintTypeMap) {
        if (viewNode.getAutofillHints() != null)
            hintTypeMap.put(viewNode.getAutofillId(), viewNode.getAutofillHints()[0]);
        int childCount = viewNode.getChildCount();
        for (int i = 0; i < childCount; i++)
            parseAllHintType(viewNode.getChildAt(i), hintTypeMap);
    }

    //创建一个表单建议对应的View
    private RemoteViews createPresentation(String name) {
        RemoteViews presentation = new RemoteViews(CommonApplication.ctx.getPackageName(), R.layout.item_autofill_option);
        presentation.setTextViewText(R.id.text, name);
        return presentation;
    }
}
