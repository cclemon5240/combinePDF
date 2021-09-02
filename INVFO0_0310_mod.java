package com.cathaybk.invf.o0.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.cathay.common.exception.ErrorInputException;
import com.cathay.common.exception.ModuleException;
import com.cathay.common.im.util.MessageUtil;
import com.cathay.common.im.util.VOTool;
import com.cathay.common.service.ConfigManager;
import com.cathay.common.util.NumberUtils;
import com.cathay.common.util.STRING;
import com.cathay.rpt.RptUtils;
import com.cathay.util.Transaction;
import com.cathaybk.invf.dao.INVF_CFG_DEFTYPE_SYS;
import com.cathaybk.invf.dao.INVF_TRANSFER_TABLE_CHECK;
import com.cathaybk.invf.dao.INVF_TRANSFER_TABLE_SET;
import com.igsapp.db.DataSet;

/**
 * <pre>
 * 轉檔比對
 * </pre>
 * @author NT86094
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class INVFO0_0310_mod {

    /** 輸出欄位 */
    private static final Map OUTPUT_COLUMN_MAP = Collections.synchronizedMap(new ListOrderedMap());

    /** 暫存檔路徑 */
    private static final String ROOT_FILE_PATH = ConfigManager.getProperty("com.cathay.util.jasper.JasperUtils.jasperReportSaveRoot");

    /** 根據Transfer_time查詢 */
    private static final String SQL_QUERY_RESULT_001 = "com.cathaybk.invf.o0.module.INVFO0_0310_mod.SQL_QUERY_RESULT_001";

    static {
        OUTPUT_COLUMN_MAP.put("TRANSFER_TIME", "轉檔比對時間");
        OUTPUT_COLUMN_MAP.put("SEQ_NO", "序號");
        OUTPUT_COLUMN_MAP.put("TABLE_ORACLE", "ORACLE");
        OUTPUT_COLUMN_MAP.put("TABLE_CTF", "SQL CTF");
        OUTPUT_COLUMN_MAP.put("TABLE_CTFL", "SQL CTFL");
        OUTPUT_COLUMN_MAP.put("TABLE_CTF_HISTORY", "SQL CTFHistory");
        OUTPUT_COLUMN_MAP.put("TABLE_CTFL_HISTORY", "SQL CTFLHistory");
        OUTPUT_COLUMN_MAP.put("TABLE_DESC", "檔案說明");
        OUTPUT_COLUMN_MAP.put("TABLE_STATUS", "TABLE STATUS");
        OUTPUT_COLUMN_MAP.put("TABLE_COUNT_FLAG", "要比對筆數?");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_SQL_1", "SQL 欄位1");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_SQL_2", "SQL 欄位2");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_SQL_3", "SQL 欄位3");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_SQL_4", "SQL 欄位4");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_SQL_5", "SQL 欄位5");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_ORACLE_1", "ORACLE 欄位1");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_ORACLE_2", "ORACLE 欄位2");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_ORACLE_3", "ORACLE 欄位3");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_ORACLE_4", "ORACLE 欄位4");
        OUTPUT_COLUMN_MAP.put("FIELD_NAME_ORACLE_5", "ORACLE 欄位5");
        OUTPUT_COLUMN_MAP.put("OWNER", "OWNER");
        OUTPUT_COLUMN_MAP.put("REMARK", "備註");
        OUTPUT_COLUMN_MAP.put("ROW_COUNT_SQL", "SQL 筆數");
        OUTPUT_COLUMN_MAP.put("ROW_COUNT_ORACLE", "ORACLE 筆數");
        OUTPUT_COLUMN_MAP.put("ROW_COUNT_RESULT", "比對筆數結果");
        OUTPUT_COLUMN_MAP.put("FIELD_SQL_1", "SQL 欄位1 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_ORACLE_1", "ORACLE 欄位1 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_RESULT_1", "欄位1 比對結果");
        OUTPUT_COLUMN_MAP.put("FIELD_SQL_2", "SQL 欄位2 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_ORACLE_2", "ORACLE 欄位2 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_RESULT_2", "欄位2 比對結果");
        OUTPUT_COLUMN_MAP.put("FIELD_SQL_3", "SQL 欄位3 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_ORACLE_3", "ORACLE 欄位3 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_RESULT_3", "欄位3 比對結果");
        OUTPUT_COLUMN_MAP.put("FIELD_SQL_4", "SQL 欄位4 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_ORACLE_4", "ORACLE 欄位4 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_RESULT_4", "欄位4 比對結果");
        OUTPUT_COLUMN_MAP.put("FIELD_SQL_5", "SQL 欄位5 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_ORACLE_5", "ORACLE 欄位5 總計");
        OUTPUT_COLUMN_MAP.put("FIELD_RESULT_5", "欄位5 比對結果");
    }

    /**
     * 查詢TABLE_STATUS
     * @return
     * @throws ModuleException
     */
    public List<Map> queryStatus() throws ModuleException {
        String[] queryfield = { "SORT_NO", "PARA_CODE", "PARA_NAME" };
        String[] orderKey = { "SORT_NO" };
        Map<String, Object> fieldMap = new HashMap();
        fieldMap.put("LEVEL1_CODE", "TRANS_SET");
        fieldMap.put("LEVEL2_CODE", "TABLE_STATUS");
        fieldMap.put("ENABLED", "Y");
        fieldMap.put("DISPLAY", "Y");
        fieldMap.put("LANGUAGE", "zh_TW");
        return INVF_CFG_DEFTYPE_SYS.getInstance().find(fieldMap, queryfield, orderKey, true);
    }

    /**
     * 查詢TRANSFER_TABLE_SET
     * @return
     * @throws ModuleException
     */
    public List<Map> queryTransferTableSet(Map<String, String> maps) throws ModuleException {

        if (maps == null || maps.isEmpty()) {
            throw new ErrorInputException("傳入參數不得為空");
        }

        String status = MapUtils.getString(maps, "status");
        if (StringUtils.isBlank(status)) {
            throw new ErrorInputException("Table Status不得為空");
        }
        Map<String, Object> fieldMap = new HashMap();
        String oracleTable = MapUtils.getString(maps, "oracleTable");
        if (StringUtils.isNotBlank(oracleTable)) {
            fieldMap.put("TABLE_ORACLE", oracleTable.trim());
        }
        String owner = MapUtils.getString(maps, "owner");
        if (StringUtils.isNotBlank(owner)) {
            fieldMap.put("OWNER", owner.trim());
        }

        switch (status) {
            case "1":
                fieldMap.put("TABLE_STATUS", "需轉檔");
                break;
            case "2":
                fieldMap.put("TABLE_STATUS", "預建資料");
                break;
            case "3":
                fieldMap.put("TABLE_STATUS", "不需轉檔");
                break;
            default:
                break;
        }
        List<Map> dataList = INVF_TRANSFER_TABLE_SET.getInstance().find(fieldMap, null, null, true);
        for (int i = 0; i < dataList.size(); i++) {
            Map dataMap = dataList.get(i);
            dataMap.put("FIELD_NAME_SQL_1", MapUtils.getString(dataMap, "FIELD_NAME_SQL_1", ""));
            dataMap.put("FIELD_NAME_SQL_2", MapUtils.getString(dataMap, "FIELD_NAME_SQL_2", ""));
            dataMap.put("FIELD_NAME_SQL_3", MapUtils.getString(dataMap, "FIELD_NAME_SQL_3", ""));
            dataMap.put("FIELD_NAME_SQL_4", MapUtils.getString(dataMap, "FIELD_NAME_SQL_4", ""));
            dataMap.put("FIELD_NAME_SQL_5", MapUtils.getString(dataMap, "FIELD_NAME_SQL_5", ""));
            dataMap.put("FIELD_NAME_ORACLE_1", MapUtils.getString(dataMap, "FIELD_NAME_ORACLE_1", ""));
            dataMap.put("FIELD_NAME_ORACLE_2", MapUtils.getString(dataMap, "FIELD_NAME_ORACLE_2", ""));
            dataMap.put("FIELD_NAME_ORACLE_3", MapUtils.getString(dataMap, "FIELD_NAME_ORACLE_3", ""));
            dataMap.put("FIELD_NAME_ORACLE_4", MapUtils.getString(dataMap, "FIELD_NAME_ORACLE_4", ""));
            dataMap.put("FIELD_NAME_ORACLE_5", MapUtils.getString(dataMap, "FIELD_NAME_ORACLE_5", ""));
        }

        return dataList;
    }

    /**
     * 查詢TRANSFER_TABLE_SET
     * @return
     * @throws ModuleException
     */
    public List<Map<String, Object>> compare(List<Map> queryList) throws ModuleException {

        if (queryList == null || queryList.isEmpty()) {
            throw new ErrorInputException("傳入參數不得為空");
        }

        List<Map<String, Object>> msDbList = new ArrayList<>();

        String[] oracleTableArr = { "FIELD_NAME_ORACLE_1", "FIELD_NAME_ORACLE_2", "FIELD_NAME_ORACLE_3", "FIELD_NAME_ORACLE_4",
                "FIELD_NAME_ORACLE_5" };
        String[] msTableArr = { "FIELD_NAME_SQL_1", "FIELD_NAME_SQL_2", "FIELD_NAME_SQL_3", "FIELD_NAME_SQL_4", "FIELD_NAME_SQL_5" };
        String[] tableSqlHeader = { "CTF", "CTFL", "CTFHistory", "CTFLHistory" };

        //計算正常異常總比數
        int normal = 0;
        int unusual = 0;
        int normalCount = 0;
        int unusualCount = 0;

        //儲存所有table比對結果
        List<Map<String, Object>> resultList = new ArrayList<>();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        INVF_TRANSFER_TABLE_CHECK theINVF_TRANSFER_TABLE_CHECK = INVF_TRANSFER_TABLE_CHECK.getInstance();
        for (Map queryMap : queryList) {
            if ("需轉檔".equals(MapUtils.getString(queryMap, "TABLE_STATUS"))
                    && "Y".equals(MapUtils.getString(queryMap, "TABLE_COUNT_FLAG"))) {

                String tableOracle = MapUtils.getString(queryMap, "TABLE_ORACLE");
                String tableCtf = MapUtils.getString(queryMap, "TABLE_CTF");
                String tableCtfl = MapUtils.getString(queryMap, "TABLE_CTFL");
                String tableCtfHistory = MapUtils.getString(queryMap, "TABLE_CTF_HISTORY");
                String tableCrflHistory = MapUtils.getString(queryMap, "TABLE_CTFL_HISTORY");
                String[] tableSql = { tableCtf, tableCtfl, tableCtfHistory, tableCrflHistory };

                //儲存table比對結果
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("TRANSFER_TIME", currentTime);
                resultMap.put("SEQ_NO", MapUtils.getString(queryMap, "SEQ_NO"));
                resultMap.put("TABLE_ORACLE", tableOracle);
                resultMap.put("TABLE_CTF", tableCtf);
                resultMap.put("TABLE_CTFL", tableCtfl);
                resultMap.put("TABLE_CTF_HISTORY", tableCtfHistory);
                resultMap.put("TABLE_CTFL_HISTORY", tableCrflHistory);
                resultMap.put("TABLE_DESC", MapUtils.getString(queryMap, "TABLE_DESC"));
                resultMap.put("TABLE_STATUS", MapUtils.getString(queryMap, "TABLE_STATUS"));
                resultMap.put("TABLE_COUNT_FLAG", MapUtils.getString(queryMap, "TABLE_COUNT_FLAG"));
                resultMap.put("OWNER", MapUtils.getString(queryMap, "OWNER"));
                resultMap.put("REMARK", MapUtils.getString(queryMap, "REMARK"));

                //將SQL Server中需要比對的欄位存在msDbList
                for (int i = 0; i < tableSql.length; i++) {
                    if (StringUtils.isNotBlank(tableSql[i])) {
                        Map<String, Object> tableMap = new HashMap<>();
                        tableMap.put("db", tableSqlHeader[i]);
                        String[] spl = tableSql[i].split("\\.");
                        tableMap.put("table", spl[spl.length - 1]);
                        msDbList.add(tableMap);
                    }
                }

                //判斷SQL Server中是否有欄位要比對
                if (msDbList.size() > 0) {
                    //計算Oracle 總比數
                    DataSet ds = Transaction.getDynamicDataSet();
                    sb.append("select count(*) as tot");
                    sb.append(" from IVTLXIVP01FUND.");
                    sb.append(tableOracle);
                    BigDecimal oraclecount = STRING.objToBigDecimal(MapUtils.getString(VOTool.findOneToMap(ds, sb.toString()), "TOT"),
                        BigDecimal.ZERO);
                    resultMap.put("ROW_COUNT_ORACLE", oraclecount);
                    sb.setLength(0);

                    //計算SQL Server總比數
                    BigDecimal mscount = BigDecimal.ZERO;
                    for (int i = 0; i < msDbList.size(); i++) {
                        mscount = mscount.add(msCount(msDbList.get(i), sb));
                    }
                    resultMap.put("ROW_COUNT_SQL", mscount);

                    //比對兩邊總比數
                    if (oraclecount.compareTo(mscount) == 0) {
                        resultMap.put("ROW_COUNT_RESULT", "S");
                        normalCount++;
                    } else {
                        resultMap.put("ROW_COUNT_RESULT", "F");
                        unusualCount++;
                    }

                    //計算Oracle金額欄位總額
                    BigDecimal[] oracleSumArr = new BigDecimal[oracleTableArr.length];
                    Arrays.fill(oracleSumArr, BigDecimal.ZERO);
                    for (int i = 0; i < oracleTableArr.length; i++) {
                        String oracleColumn = MapUtils.getString(queryMap, oracleTableArr[i], "0");
                        resultMap.put(oracleTableArr[i], oracleColumn);
                        if (!("0".equals(oracleColumn)) && StringUtils.isNotBlank(oracleColumn)) {
                            sb.append("select sum(cast(coalesce(");
                            sb.append(oracleColumn);
                            sb.append(",0) as NUMBER(19,4))) as sum from IVTLXIVP01FUND.");
                            sb.append(tableOracle);
                            BigDecimal oracleSum = STRING.objToBigDecimal(
                                MapUtils.getString(VOTool.findOneToMap(ds, sb.toString()), "SUM", "0"), BigDecimal.ZERO);
                            oracleSumArr[i] = oracleSum;
                            resultMap.put("FIELD_ORACLE_" + (i + 1), oracleSum);
                            sb.setLength(0);
                        }
                    }

                    //計算SQL Server金額欄位總額
                    BigDecimal[] msSumArr = new BigDecimal[msTableArr.length];
                    Arrays.fill(msSumArr, BigDecimal.ZERO);
                    //db & table
                    for (int i = 0; i < msDbList.size(); i++) {
                        //column
                        for (int j = 0; j < msTableArr.length; j++) {
                            String mscolumn = MapUtils.getString(queryMap, msTableArr[j], "0");
                            resultMap.put(msTableArr[j], mscolumn);
                            if (!("0".equals(mscolumn)) && StringUtils.isNotBlank(mscolumn)) {
                                Map<String, Object> msTableMap = msDbList.get(i);
                                msTableMap.put("column", mscolumn);
                                msSumArr[j] = msSumArr[j].add(msSum(msTableMap, sb));
                                msTableMap.remove("column");
                                resultMap.put("FIELD_SQL_" + (j + 1), msSumArr[j]);
                            }
                        }
                    }

                    //金額欄位比對結果
                    for (int i = 0; i < msTableArr.length; i++) {
                        String mscolumn = MapUtils.getString(queryMap, msTableArr[i], "-1");
                        if (!("0".equals(mscolumn)) && StringUtils.isNotBlank(mscolumn)) {
                            if (oracleSumArr[i].compareTo(msSumArr[i]) == 0) {
                                resultMap.put("FIELD_RESULT_" + (i + 1), "S");
                                normalCount++;
                            } else {
                                resultMap.put("FIELD_RESULT_" + (i + 1), "F");
                                unusualCount++;
                            }
                        }
                    }
                }

                Transaction.begin();
                try {
                    theINVF_TRANSFER_TABLE_CHECK.insert(resultMap);
                    Transaction.commit();
                } catch (Exception e) {
                    Transaction.rollback();
                    throw e;
                }
                if (unusualCount > 0) {
                    unusual++;
                } else {
                    normal++;
                }

                resultList.add(resultMap);
                msDbList.clear();
                normalCount = 0;
                unusualCount = 0;
            }
        }
        Map<String, Object> mapCount = new HashMap();
        mapCount.put("normal", normal);
        mapCount.put("unusual", unusual);
        resultList.add(mapCount);

        return resultList;
    }

    /**
     * 查詢比對結果
     * @return
     * @throws ModuleException
     */
    public List<Map> queryResult(String transfer_time) throws ModuleException {
        DataSet ds = Transaction.getDataSet();
        if (StringUtils.isEmpty(transfer_time)) {
            throw new ErrorInputException("無資料輸出");
        }
        ds.setField("TRANSFER_TIME", transfer_time.substring(0, transfer_time.lastIndexOf(".")));
        return VOTool.findToMaps(ds, SQL_QUERY_RESULT_001, true);
    }

    /**
     * <pre>
     * 匯出Excel
     * </pre>
     * @param result
     * @param fileName
     * @param outPutColumnMap
     * @param sheetName
     * @return
     * @throws ModuleException
     * @throws IOException
     */
    public String[] export(List<Map> result, String fileName, String sheetName, boolean autoNewLine) throws ModuleException, IOException {

        if (result == null) {
            throw new ErrorInputException(MessageUtil.getMessage("INVFZ0Z001_MSG_001")); // 無資料可下載
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook();
        if (sheetName == null) {
            sheetName = fileName;
        }
        Sheet sheet = workbook.createSheet(sheetName);

        // 寫入資料
        createworkbook(workbook, sheet, result, OUTPUT_COLUMN_MAP, autoNewLine);
        // 設定欄位大小
        getSheetColumnWidth(sheet, OUTPUT_COLUMN_MAP);

        // 產生暫存檔
        String fullFileName = getFileName(fileName);
        File downloadFile = RptUtils.createTempFile(fullFileName);

        //產生暫存資料夾
        File file = new File(ROOT_FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        // 輸出 excel 檔的路徑
        String path = downloadFile.getPath();
        try (FileOutputStream fileOutputString = new FileOutputStream(path)) {
            workbook.write(fileOutputString);
        }
        return new String[] { fullFileName, path };
    }

    /**
     * <pre>
     * 設定excel欄位
     * </pre>
     * @param sheet
     * @param outPutColumnMap
     */
    private void getSheetColumnWidth(Sheet sheet, Map outPutColumnMap) {
        int totalColumnSize = outPutColumnMap.size();
        for (int i = 0; i < totalColumnSize; i++) {
            sheet.setColumnWidth(i, 30 * 256);
        }
    }

    /**
     * <pre>
     * 設定欄位stytle 欄位名稱
     * </pre>
     * @param workbook
     * @param sheet
     * @param rtnList
     * @param outPutColumnMap
     */
    private void createworkbook(SXSSFWorkbook workbook, Sheet sheet, List<Map> rtnList, Map outPutColumnMap, boolean autoNewLine) {
        int beginRow = 0;
        // 設定STYLE
        CellStyle style0 = null;
        //第一列標題
        Row firstRow = sheet.createRow(beginRow);
        Iterator<String> iter = outPutColumnMap.values().iterator();
        //標題內容輸入
        int i = 0;
        while (iter.hasNext()) {
            if (i == 22 || i == 23 || i == 24) {
                style0 = createStyle(workbook, 5, "新細明體");
            } else {
                style0 = createStyle(workbook, 0, "新細明體");
            }
            setColumn(firstRow, style0, i, iter.next(), false);
            i++;
        }
        beginRow++;
        //明細資料(不含合計)
        createDetail(beginRow, workbook, sheet, rtnList, outPutColumnMap, autoNewLine);
        sheet.autoSizeColumn(1);
        sheet.setAutoFilter(CellRangeAddress.valueOf("B1"));
    }

    /**
     * <pre>
     * 設定stytle
     * </pre>
     * @param workbook
     * @param type
     * @param fontType
     * @return
     */
    private CellStyle createStyle(SXSSFWorkbook workbook, int type, String fontType) {
        // Style
        CellStyle style = workbook.createCellStyle();
        // 字型
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12); // 大小
        // 字型顏色
        font.setColor(HSSFColor.BLACK.index);
        font.setFontName(fontType);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直置中 
        if (type == 0) {// 第一列 大標頭  綠
            font.setColor(HSSFColor.WHITE.index);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 置中
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 背景
            style.setFillForegroundColor(HSSFColor.GREEN.index);// 背景顏色
        } else if (type == 1) { // 數值
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        } else if (type == 2) { // 數值
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (type == 3) { // 數值
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            font.setColor(HSSFColor.RED.index);
        } else if (type == 4) { // 數值
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            font.setColor(HSSFColor.RED.index);
        } else if (type == 5) {
            font.setColor(HSSFColor.WHITE.index);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 置中
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 背景
            style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);// 背景顏色
        } else { // 文字
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        }

        style.setFont(font);
        //外框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);

        return style;
    }

    /**
     * <pre>
     * 設定cell
     * </pre>
     * @param bodyRow
     * @param style
     * @param columnNumber
     * @param content
     * @param isNumeric
     */
    private void setColumn(Row bodyRow, CellStyle style, Integer columnNumber, String content, boolean isNumeric) {
        Cell bodyCell = bodyRow.createCell(columnNumber);
        if (isNumeric) { // 數值欄位
            if (StringUtils.isNotBlank(content) && NumberUtils.isNumber(content)) {
                bodyCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                Double bodyText = new Double(content);
                bodyCell.setCellValue(bodyText);
            }
        } else {
            HSSFRichTextString text = new HSSFRichTextString(content);
            bodyCell.setCellValue(text);
        }
        bodyCell.setCellStyle(style);
    }

    /**
     * <pre>
     * 設定欄位名稱
     * </pre>
     * @param beginRow
     * @param workbook
     * @param sheet
     * @param row
     * @param rtnList
     * @param totalColumns
     * @param outPutColumnMap
     */
    private void createDetail(int beginRow, SXSSFWorkbook workbook, Sheet sheet, List<Map> rtnList, Map outPutColumnMap,
            boolean autoNewLine) {

        // 文字 黑色 
        CellStyle style1 = createStyle(workbook, 1, "新細明體"); //一般置左
        CellStyle style2 = createStyle(workbook, 2, "新細明體"); //一般置中
        CellStyle style3 = createStyle(workbook, 3, "新細明體"); //灰色色置左
        CellStyle style4 = createStyle(workbook, 4, "新細明體"); //灰色置中
        String[] check = { "ROW_COUNT_RESULT", "FIELD_RESULT_1", "FIELD_RESULT_2", "FIELD_RESULT_3", "FIELD_RESULT_4", "FIELD_RESULT_5" };
        int unusual = 0;

        for (int i = 0; i < rtnList.size(); i++) {
            Row row = sheet.createRow(beginRow);

            CellStyle style = null;
            boolean flag = false;
            for (int j = 0; j < check.length; j++) {
                String checks = MapUtils.getString(rtnList.get(i), check[j], "").trim();
                if ("F".equals(checks)) {
                    flag = true;
                    unusual++;
                    break;
                }

            }
            Iterator<String> iter = outPutColumnMap.keySet().iterator();
            int k = 0;
            while (iter.hasNext()) {
                String fieldName = iter.next();
                String columnContent = MapUtils.getString(rtnList.get(i), fieldName, "").trim();

                if (flag) {
                    switch (k) {
                        case 24:
                            if ("F".equals(columnContent)) {
                                columnContent = "筆數不合";
                            } else if ("S".equals(columnContent)) {
                                columnContent = "成功";
                            }
                            style = style4;//灰底紅字置中
                            break;
                        case 27:
                        case 30:
                        case 33:
                        case 36:
                        case 39:
                            if ("F".equals(columnContent)) {
                                columnContent = "數值不合";
                            } else if ("S".equals(columnContent)) {
                                columnContent = "成功";
                            }
                            style = style4;//灰底紅字置中
                            break;
                        default:
                            style = style3;//灰底紅字置左
                    }
                } else {
                    switch (k) {
                        case 24:
                        case 27:
                        case 30:
                        case 33:
                        case 36:
                        case 39:
                            if ("S".equals(columnContent)) {
                                columnContent = "成功";
                            }
                            style = style2;//置中
                            break;
                        default:
                            style = style1;//置左
                    }
                }
                style.setWrapText(autoNewLine);
                setColumn(row, style, k, columnContent, false);
                k++;
            }
            beginRow++;
        }
        Row row = sheet.createRow(beginRow);
        int size = rtnList.size();

        setColumn(row, style2, 0, "全部", false);
        setColumn(row, style2, 1, String.valueOf(size), false);
        setColumn(row, style2, 2, "正常", false);
        setColumn(row, style2, 3, String.valueOf(size - unusual), false);
        setColumn(row, style2, 4, "異常", false);
        setColumn(row, style2, 5, String.valueOf(unusual), false);

    }

    /**匯出檔案名稱
     * @param fileName
     * @return
     */
    private String getFileName(String fileName) {
        StringBuilder sb = new StringBuilder();
        String createDate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        return sb.append(createDate).append("_").append(fileName).append(".xlsx").toString();
    }

    /**
     * SQLServer計算總比數
     * @return
     * @throws ModuleException
     */
    private BigDecimal msCount(Map tablename, StringBuilder sb) throws ModuleException {

        DataSet ds = Transaction.getDynamicDataSet();
        ds.setConnName("DS_INVF1");
        sb.append("select count(*) as tot");
        sb.append(" from ");
        sb.append(tablename.get("db"));
        sb.append(".dbo.");
        sb.append(tablename.get("table"));
        if ("CustomerLogF".equals(MapUtils.getString(tablename, "table")) && "CTF".equals(MapUtils.getString(tablename, "db"))) {
            sb.append(" where DataBefore <> 'IVTNTCIT01'");
        } else if (("CustomerSign".equals(MapUtils.getString(tablename, "table"))
                || "CustomerSignLog".equals(MapUtils.getString(tablename, "table"))) && "CTF".equals(MapUtils.getString(tablename, "db"))) {
            sb.append(" where TxSys='CTF'");
        }
        String sql = sb.toString();
        sb.setLength(0);
        return new BigDecimal((Integer) VOTool.findOneToMap(ds, sql).get("TOT"));
    }

    /**
     * SQLServer計算總金額
     * @param columnname
     * @return
     * @throws ModuleException
     */
    private BigDecimal msSum(Map columnname, StringBuilder sb) throws ModuleException {

        DataSet ds = Transaction.getDynamicDataSet();
        ds.setConnName("DS_INVF1");
        sb.append("select sum(cast(coalesce(");
        sb.append(columnname.get("column"));
        sb.append(",0) as decimal(19,4))) as sum from ");
        sb.append(columnname.get("db"));
        sb.append(".dbo.");
        sb.append(columnname.get("table"));
        if ("CustomerLogF".equals(MapUtils.getString(columnname, "table")) && "CTF".equals(MapUtils.getString(columnname, "db"))) {
            sb.append(" where DataBefore <> 'IVTNTCIT01'");
        } else if (("CustomerSign".equals(MapUtils.getString(columnname, "table"))
                || "CustomerSignLog".equals(MapUtils.getString(columnname, "table")))
                && "CTF".equals(MapUtils.getString(columnname, "db"))) {
            sb.append(" where TxSys='CTF'");
        }
        Map resultMap = VOTool.findOneToMap(ds, sb.toString());
        sb.setLength(0);
        return STRING.objToBigDecimal(resultMap.get("SUM"), BigDecimal.ZERO);
    }
}
