package com.cathaybk.invf.o0.trx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cathay.common.exception.DataNotFoundException;
import com.cathay.common.exception.ErrorInputException;
import com.cathay.common.exception.ModuleException;
import com.cathay.common.exception.OverCountLimitException;
import com.cathay.common.im.util.MessageUtil;
import com.cathay.common.im.util.VOTool;
import com.cathay.rpt.RptUtils;
import com.cathay.util.ReturnCode;
import com.cathaybk.common.trx.UCBean;
import com.cathaybk.invf.o0.module.INVFO0_0310_mod;
import com.igsapp.common.util.annotation.CallMethod;
import com.igsapp.common.util.annotation.TxBean;
import com.igsapp.wibc.dataobj.Context.RequestContext;
import com.igsapp.wibc.dataobj.Context.ResponseContext;

/**
 * <pre>
 * 轉檔比對
 * </pre>
 * @author NT86094
 *
 */
@TxBean
public class INVFO0_0310 extends UCBean {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(INVFO0_0310.class);

    /**
     * 載入初始頁面
     * @param req
     * @return
     */
    @CallMethod(action = "prompt", url = "/cathaybk/system/invf/o0/INVFO0_0300/INVFO00310.jsp", name = CallMethod.CODE_SUCCESS, type = CallMethod.TYPE_SUBMIT)
    public ResponseContext doPrompt(RequestContext req) {
        try {
            //            String eBAF_displayLanguage = LocaleUtil.getCurrentLocale().toString();
            INVFO0_0310_mod theINVFO0_0310_mod = new INVFO0_0310_mod();
            resp.addOutputData("tableStatus", theINVFO0_0310_mod.queryStatus());
            Map<String, String> maps = new HashMap<>();
            maps.put("status", "1");
            resp.addOutputData("resultList", theINVFO0_0310_mod.queryTransferTableSet(maps));
            MessageUtil.setMsg(msg, "查詢成功");
        } catch (DataNotFoundException dnfe) {
            log.error(dnfe);
            MessageUtil.setReturnMessage(msg, ReturnCode.DATA_NOT_FOUND, "查無資料");
        } catch (ModuleException me) {
            if (me.getRootException() == null) {
                log.error("", me);
                MessageUtil.setReturnMessage(msg, ReturnCode.ERROR_MODULE, me.getMessage());
            } else {
                log.error(me.getMessage(), me.getRootException());
                if (me.getRootException() instanceof OverCountLimitException) {
                    MessageUtil.setReturnMessage(msg, me, req, ReturnCode.ERROR_MODULE, "MSG_OVER_COUNT_LIMIT"); //查詢筆數超出系統限制，請縮小查詢範圍
                } else {
                    MessageUtil.setReturnMessage(msg, me, req, ReturnCode.ERROR_MODULE, "MSG_QUERY_FAIL"); //查詢失敗
                }
            }
        } catch (Exception e) {
            String errMsg = MessageUtil.getMessage("MSG_INITIAL_FAIL");
            log.error(errMsg, e);
            MessageUtil.setReturnMessage(msg, e, req, ReturnCode.ERROR, errMsg);
        }
        return resp;
    }

    /**
     * 查詢Table Status
     * @param req
     * @return
     */
    @CallMethod(action = "querySta", type = CallMethod.TYPE_AJAX)
    public ResponseContext doQuerySta(RequestContext req) {
        try {
            resp.addOutputData("resultList", new INVFO0_0310_mod().queryTransferTableSet(VOTool.requestToMap(req)));
            MessageUtil.setMsg(msg, "查詢成功");
        } catch (ErrorInputException eie) {
            log.error(eie);
            MessageUtil.setReturnMessage(msg, ReturnCode.ERROR_INPUT, eie.getMessage());
        } catch (DataNotFoundException dnfe) {
            log.error(dnfe);
            MessageUtil.setReturnMessage(msg, ReturnCode.DATA_NOT_FOUND, dnfe.getMessage());
        } catch (ModuleException me) {
            if (me.getRootException() == null) {
                log.error("", me);
                MessageUtil.setReturnMessage(msg, ReturnCode.ERROR_MODULE, me.getMessage());
            } else {
                log.error(me.getMessage(), me.getRootException());
                if (me.getRootException() instanceof OverCountLimitException) {
                    MessageUtil.setReturnMessage(msg, me, req, ReturnCode.ERROR_MODULE, "MSG_OVER_COUNT_LIMIT"); //查詢筆數超出系統限制，請縮小查詢範圍
                } else {
                    MessageUtil.setReturnMessage(msg, me, req, ReturnCode.ERROR_MODULE, "MSG_QUERY_FAIL"); //查詢失敗
                }
            }
        } catch (Exception e) {
            String errMsg = MessageUtil.getMessage("MSG_QUERY_FAIL"); //查詢失敗
            log.error(errMsg, e);
            MessageUtil.setReturnMessage(msg, e, req, ReturnCode.ERROR, errMsg);
        }
        return resp;
    }

    /**
     * 比較兩DB
     * @param req
     * @return
     */
    @CallMethod(action = "compare", type = CallMethod.TYPE_AJAX)
    public ResponseContext doCompare(RequestContext req) {
        try {
            List<Map<String, Object>> resultList = new INVFO0_0310_mod().compare(VOTool.jsonAryToMaps(req.getParameter("getCheck")));
            int size = resultList.size();
            resp.addOutputData("normal", resultList.get(size - 1).get("normal"));
            resp.addOutputData("unusual", resultList.get(size - 1).get("unusual"));
            resp.addOutputData("all", size - 1);
            resultList.remove(size - 1);
            resp.addOutputData("resultList", resultList);

            MessageUtil.setMsg(msg, "比對成功");
        } catch (ErrorInputException eie) {
            log.error(eie);
            MessageUtil.setReturnMessage(msg, ReturnCode.ERROR_INPUT, eie.getMessage());
        } catch (DataNotFoundException dnfe) {
            log.error(dnfe);
            MessageUtil.setReturnMessage(msg, ReturnCode.DATA_NOT_FOUND, dnfe.getMessage());
        } catch (ModuleException me) {
            if (me.getRootException() == null) {
                log.error("", me);
                MessageUtil.setReturnMessage(msg, ReturnCode.ERROR_MODULE, me.getMessage());
            } else {
                log.error(me.getMessage(), me.getRootException());
                if (me.getRootException() instanceof OverCountLimitException) {
                    MessageUtil.setReturnMessage(msg, me, req, ReturnCode.ERROR_MODULE, "MSG_OVER_COUNT_LIMIT"); //查詢筆數超出系統限制，請縮小查詢範圍
                } else {
                    MessageUtil.setReturnMessage(msg, me, req, ReturnCode.ERROR_MODULE, "MSG_QUERY_FAIL"); //查詢失敗
                }
            }
        } catch (Exception e) {
            String errMsg = MessageUtil.getMessage("MSG_QUERY_FAIL"); //查詢失敗
            log.error(errMsg, e);
            MessageUtil.setReturnMessage(msg, e, req, ReturnCode.ERROR, errMsg);
        }
        return resp;
    }

    /**
     * <pre>
     * 匯出Excel
     * </pre>
     * @param req
     * @return
     */
    @CallMethod(action = "export", type = CallMethod.TYPE_AJAX)
    public ResponseContext doExport(RequestContext req) {

        try {
            INVFO0_0310_mod theINVFO0_0310_mod = new INVFO0_0310_mod();
            String[] fileInfo = theINVFO0_0310_mod.export(theINVFO0_0310_mod.queryResult(req.getParameter("TRANSFER_TIME")), "轉檔比對結果", null,
                false);
            RptUtils.cryptoDownloadParameterToResp(fileInfo[0], fileInfo[1], resp);
        } catch (Exception e) {
            String errMsg = MessageUtil.getMessage("MSG_EXPORT_FAIL");//匯出失敗
            log.error(errMsg, e);
            MessageUtil.setReturnMessage(msg, e, req, ReturnCode.ERROR, errMsg);
        }
        return resp;
    }

}
