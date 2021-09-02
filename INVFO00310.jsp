<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="UTF-8" />
<%@ include file="/html/cathaybk/common/header.jsp"%>
<title>${eBAF_cathaybkSystemInfo.systemName}</title>
<link rel="stylesheet" type="text/css" href="${htmlBase}/cathaybk/common/css/ui/skeleton.css">
<link rel="stylesheet" type="text/css" href="${htmlBase}/cathaybk/common/css/ui/TableUI.css">
<link rel="stylesheet" type="text/css" href="${htmlBase}/cathaybk/common/css/ui/commons.css">
<script type="text/javascript" src="${htmlBase}/CM/js/jq/jquery.js"></script>
<script type="text/javascript" src="${htmlBase}/CM/js/ajax/CSRUtil.js"></script>
<script type="text/javascript" src="${htmlBase}/cathaybk/common/js/ui/MsgBox.js"></script>
<script type="text/javascript" src="${htmlBase}/cathaybk/common/js/ui/TableUI.js"></script>
<script type="text/javascript" src="${htmlBase}/cathaybk/common/js/ZRUtil.js"></script>
<script type="text/javascript" src="${htmlBase}/cathaybk/common/js/ui/Validate.js"></script>
<script>
	var INVFO0_0310 = new function() {
	
	    var _funcId = 'INVFO0_0310';
	    var _funcName = '轉檔比對';
	    var ajaxRequest = new CSRUtil.AjaxHandler.request('${dispatcher}/INVFO0_0310/');
	    return {
	        initApp: function() {
	            $('#functionDescription').html(_funcId + '－' + _funcName);
	            
	            var pageChangeArr = ['pageSizeBlock', 'iconFirst', 'iconPrevious', 'iconNext', 'iconLast'];
	            
	            var valid_compare = new Validate();
	            valid_compare.register({
				    'msgDisplayObj': [
				        {ele: $('#compare'), errmsg: '請選擇欲比對資料'}
				    ],
				    'checkRule': function() {
						var flag = false;
                 	    
                 	    $('input[type="checkbox"]').each(function (index, e) {
	   				        
	   				        if($(e).prop('checked')) {
	   				            flag = true;
	   				            return false;
	   				        }
	   				    });
                 	    
                 	    return flag;
				    }
				});
	            
	            <%-- 產生Table Status下拉選單 --%>
<c:forEach items="${tableStatus}" var="sta">
				$('#selectStatus').append($('<option/>').html('${sta.PARA_NAME}').val('${sta.PARA_CODE}'));
</c:forEach>
				$('#selectStatus').val('1');
	            
	            <%-- 轉檔勾選table --%>
	            var _tableUI = new TableUI({
					table: $('#tableChoose'),
			        column: [ 
			           { header: '', key: '', align: 'center', createCheckbox: {
			               checkAllClickFunc: function (isChecked, records) {
			                   for (var i = 0; i < records.length; i++) {
			                     records[i]['isChecked'] = isChecked;
			                   }                            
		                   }
                       }}, 
			           { header: '序號', key: 'SEQ_NO', align: 'left', isNowrap:true}, 
			           { header: 'ORACLE', key: 'TABLE_ORACLE', align: 'left', isNowrap:true}, 
			           { header: 'SQL CTF', key: 'TABLE_CTF', align: 'left', isNowrap:true},
			           { header: 'SQL CTFL', key: 'TABLE_CTFL', align: 'left', isNowrap:true},
			           { header: 'SQL CTFHistory', key: 'TABLE_CTF_HISTORY', align: 'left', isNowrap:true},
			           { header: 'SQL CTFLHistory', key: 'TABLE_CTFL_HISTORY', align: 'left', isNowrap:true},
			           { header: '檔案說明', key: 'TABLE_DESC', align: 'left', isNowrap:true},
			           { header: 'TABLE_STATUS', key: 'TABLE_STATUS', align: 'left', isNowrap:true},
			           { header: '要比對筆數?', key: 'TABLE_COUNT_FLAG', align: 'left', isNowrap:true},
			           { header: 'OWNER', key: 'OWNER', align: 'center', isNowrap:true},
			           { header: '備註', key: 'REMARK', align: 'center', isNowrap:true},
			           { header: 'SQL 欄位1', key: 'FIELD_NAME_SQL_1', align: 'left', isNowrap:true},
			           { header: 'SQL 欄位2', key: 'FIELD_NAME_SQL_2', align: 'left', isNowrap:true},
			           { header: 'SQL 欄位3', key: 'FIELD_NAME_SQL_3', align: 'left', isNowrap:true},
			           { header: 'SQL 欄位4', key: 'FIELD_NAME_SQL_4', align: 'left', isNowrap:true},
			           { header: 'SQL 欄位5', key: 'FIELD_NAME_SQL_5', align: 'left', isNowrap:true},
			           { header: 'ORACLE 欄位1', key: 'FIELD_NAME_ORACLE_1', align: 'left', isNowrap:true},
			           { header: 'ORACLE 欄位2', key: 'FIELD_NAME_ORACLE_2', align: 'left', isNowrap:true},
			           { header: 'ORACLE 欄位3', key: 'FIELD_NAME_ORACLE_3', align: 'left', isNowrap:true},
			           { header: 'ORACLE 欄位4', key: 'FIELD_NAME_ORACLE_4', align: 'left', isNowrap:true},
			           { header: 'ORACLE 欄位5', key: 'FIELD_NAME_ORACLE_5', align: 'left', isNowrap:true}
		            ],
		            pageInfo:{
						size:5
		            }
				});
	          
	            var compareNumber = function(value) {
	            	if(value == 'S') {
	                   	return '成功';
	               	}else if(value == 'F') {
	                   	return '數值不合';
	               	}
	            };
	            
				<%-- 轉檔結果table --%>
              	var result_tableUI = new TableUI({
					table: $('#tableResult'),
	                allSortable: false,
			        column: [
						{ header: '轉檔比對時間', key: 'TRANSFER_TIME', align: 'left', isNowrap:true},
			            { header: '序號', key: 'SEQ_NO', align: 'left', isNowrap:true},
			            { header: 'ORACLE', key: 'TABLE_ORACLE', align: 'left', isNowrap:true}, 
			            { header: 'SQL CTF', key: 'TABLE_CTF', align: 'left', isNowrap:true},
			            { header: 'SQL CTFL', key: 'TABLE_CTFL', align: 'left', isNowrap:true},
			            { header: 'SQL CTFHistory', key: 'TABLE_CTF_HISTORY', align: 'left', isNowrap:true},
			            { header: 'SQL CTFLHistory', key: 'TABLE_CTFL_HISTORY', align: 'left', isNowrap:true},
			            { header: '檔案說明', key: 'TABLE_DESC', align: 'left', isNowrap:true},
			            { header: 'TABLE_STATUS', key: 'TABLE_STATUS', align: 'left', isNowrap:true},
			            { header: '要比對筆數?', key: 'TABLE_COUNT_FLAG', align: 'left', isNowrap:true},
			            { header: 'OWNER', key: 'OWNER', align: 'left', isNowrap:true},
			            { header: '備註', key: 'REMARK', align: 'left', isNowrap:true},
			            { header: 'SQL 欄位1', key: 'FIELD_NAME_SQL_1', align: 'left', isNowrap:true},
			            { header: 'SQL 欄位2', key: 'FIELD_NAME_SQL_2', align: 'left', isNowrap:true},
			            { header: 'SQL 欄位3', key: 'FIELD_NAME_SQL_3', align: 'left', isNowrap:true},
			            { header: 'SQL 欄位4', key: 'FIELD_NAME_SQL_4', align: 'left', isNowrap:true},
			            { header: 'SQL 欄位5', key: 'FIELD_NAME_SQL_5', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位1', key: 'FIELD_NAME_ORACLE_1', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位2', key: 'FIELD_NAME_ORACLE_2', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位3', key: 'FIELD_NAME_ORACLE_3', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位4', key: 'FIELD_NAME_ORACLE_4', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位5', key: 'FIELD_NAME_ORACLE_5', align: 'left', isNowrap:true},
			            { header: 'SQL 筆數', key: 'ROW_COUNT_SQL', align: 'left', isNowrap:true},
			            { header: 'ORACLE 筆數', key: 'ROW_COUNT_ORACLE', align: 'left', isNowrap:true},
			            { header: '比對筆數結果', key: 'ROW_COUNT_RESULT', align: 'center', isNowrap:true, render: function(record, value, index){
			                if(value == 'S') {
			                   	return '成功';
			               	}else if(value == 'F') {
			                   	return '筆數不合';
			               	}
			            }},
			            { header: 'SQL 欄位1  總計', key: 'FIELD_SQL_1', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位1  總計', key: 'FIELD_ORACLE_1', align: 'left', isNowrap:true},
			            { header: '欄位1  比對結果', key: 'FIELD_RESULT_1', align: 'center', isNowrap:true, render: function(record, value, index){
			                return compareNumber(value);
			            }},
			            { header: 'SQL 欄位2  總計', key: 'FIELD_SQL_2', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位2  總計', key: 'FIELD_ORACLE_2', align: 'left', isNowrap:true},
			            { header: '欄位2  比對結果', key: 'FIELD_RESULT_2', align: 'center', isNowrap:true, render: function(record, value, index){
			                return compareNumber(value);
			            }},
			            { header: 'SQL 欄位3  總計', key: 'FIELD_SQL_3', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位3  總計', key: 'FIELD_ORACLE_3', align: 'left', isNowrap:true},
			            { header: '欄位3  比對結果', key: 'FIELD_RESULT_3', align: 'center', isNowrap:true, render: function(record, value, index){
			                return compareNumber(value);
			            }},
			            { header: 'SQL 欄位4  總計', key: 'FIELD_SQL_4', align: 'left', isNowrap:true},
			            { header: 'ORACLE 欄位4  總計', key: 'FIELD_ORACLE_4', align: 'left', isNowrap:true},
			            { header: '欄位4  比對結果', key: 'FIELD_RESULT_4', align: 'center', isNowrap:true, render: function(record, value, index){
			                return compareNumber(value);
			           	}},
			           	{ header: 'SQL 欄位5  總計', key: 'FIELD_SQL_5', align: 'left', isNowrap:true},
			           	{ header: 'ORACLE 欄位5  總計', key: 'FIELD_ORACLE_5', align: 'left', isNowrap:true},
			           	{ header: '欄位5  比對結果', key: 'FIELD_RESULT_5', align: 'center', isNowrap:true, render: function(record, value, index){
			           	 	return compareNumber(value);
						}},
		            ],
		            pageInfo:{
				          size:5
					}
				});
              
	          	var arr = [];
<c:forEach var="resultList" items="${resultList}">
   	          	var obj = {};
   	          	obj['SEQ_NO'] = '${resultList.SEQ_NO}';
   	          	obj['TABLE_ORACLE'] = '${resultList.TABLE_ORACLE}';
   	          	obj['TABLE_CTF'] = '${resultList.TABLE_CTF}';
   	          	obj['TABLE_CTFL'] = '${resultList.TABLE_CTFL}';
   	          	obj['TABLE_CTF_HISTORY'] = '${resultList.TABLE_CTF_HISTORY}';
   	          	obj['TABLE_CTFL_HISTORY'] = '${resultList.TABLE_CTFL_HISTORY}';
   	          	obj['TABLE_DESC'] = '${resultList.TABLE_DESC}';
   	          	obj['TABLE_STATUS'] = '${resultList.TABLE_STATUS}';
   	          	obj['TABLE_COUNT_FLAG'] = '${resultList.TABLE_COUNT_FLAG}';
   	          	obj['OWNER'] = '${resultList.OWNER}';
   	          	obj['REMARK'] = '${resultList.REMARK}';
   	          	obj['FIELD_NAME_SQL_1'] = '${resultList.FIELD_NAME_SQL_1}';
   	          	obj['FIELD_NAME_SQL_2'] = '${resultList.FIELD_NAME_SQL_2}';
   	          	obj['FIELD_NAME_SQL_3'] = '${resultList.FIELD_NAME_SQL_3}';
   	          	obj['FIELD_NAME_SQL_4'] = '${resultList.FIELD_NAME_SQL_4}';
   	          	obj['FIELD_NAME_SQL_5'] = '${resultList.FIELD_NAME_SQL_5}';
   	          	obj['FIELD_NAME_ORACLE_1'] = '${resultList.FIELD_NAME_ORACLE_1}';
   	          	obj['FIELD_NAME_ORACLE_2'] = '${resultList.FIELD_NAME_ORACLE_2}';
   	          	obj['FIELD_NAME_ORACLE_3'] = '${resultList.FIELD_NAME_ORACLE_3}';
   	          	obj['FIELD_NAME_ORACLE_4'] = '${resultList.FIELD_NAME_ORACLE_4}';
   	          	obj['FIELD_NAME_ORACLE_5'] = '${resultList.FIELD_NAME_ORACLE_5}';
   	          	arr.push(obj);
</c:forEach>
              <%-- 將有問題資料改紅色 --%>
              var changeRed = function() {
                  var colorArr = [24, 27, 30, 33, 36, 39];
                  for(var i = 1; i < $('#tableResult').find('tr').length; i ++) {
                      var tableTr = $('#tableResult').find('tr').eq(i);
                      for(var j = 0; j < colorArr.length; j ++) {
                          var innerText = tableTr.find('td').eq(colorArr[j]).children().html();
                      	  if(innerText === '筆數不合' || innerText === '數值不合'){
                      	      tableTr.css({'background-color':'#bebebe', 'color':'red'});
                          }
                      }
                  }
                  
                  var header = $('#tableResult').find('tr').eq(0).find('td');
                  var backgroundColorObj = { 'background-color':'#2894ff' };
                  header.eq(22).css(backgroundColorObj);
                  header.eq(23).css(backgroundColorObj);
                  header.eq(24).css(backgroundColorObj);
              };
              
              <%-- 查詢狀態按鈕 --%>
              $('#btn_querySta').click(function(){
                  _tableUI.clear();
                  ajaxRequest.post('querySta',{
                      status : $('#selectStatus').val(),
                      oracleTable : $('#oracleTable').val(),
                      owner : $('#owner').val()
                  },function(resp){
                      _tableUI.load(resp.resultList);
                      ($('#selectStatus').val() == '1' || $('#selectStatus').val() == 'ALL') ? $('#compare').show() : $('#compare').hide();
                      $('#showCompareTable').hide();
                  });
              });
              
	          <%-- 比較 --%>
	          $('#compare').click(function(){
	              
	              if(!valid_compare.executeCheck()){
                      return;
                  }
	              
	              ajaxRequest.post('compare',{
                      getCheck : JSON.stringify(_tableUI.getRecordsByChecked())
                  },function(resp){
                      $('#showCompareTable').show();
                      result_tableUI.load(resp.resultList);
                      $('#all').html(resp.all);
                      $('#normal').html(resp.normal);
                      $('#unusual').html(resp.unusual);
                      changeRed();
                      <%-- 將換頁按鈕註冊顏色改變 --%>
                      for(var i = 0; i < pageChangeArr.length; i ++) {
                          $('.' + pageChangeArr[i]).click(function(){
                              changeRed();
                          });
                      }
                  });
	          });
	          
	          <%-- 匯出excel --%>
	          $('#btn_export').click(function(){
	              
                  ZRUtil.downloadFile('${dispatcher}/INVFO0_0310/export',{
                      TRANSFER_TIME : $('#tableResult').find('tr').eq(1).find('td').eq(0).children().html()
                  }, false);
              });
	          
	          _tableUI.load(arr);
	          
	    	  $('#showCompareTable').hide();
	        }
	    }
	}
	$(INVFO0_0310.initApp);
</script>
</head>
<body>
	<%@ include file="/html/cathaybk/common/skeleton-header.jsp"%>
	<%@ include file="/html/cathaybk/common/skeleton-sidebar.jsp"%>
	<article>
		<div class="function-description" id="functionDescription"></div>
		<div class="main-content-wrapper wrapper-block">
			<fieldset>
				<legend>比對狀態</legend>
				<table class="formGrid">			
					<tr>
						<td class="formGrid-title">Table Status</td>
						<td class="formGrid-content">
							<select id="selectStatus" name="selectStatus"></select>
						</td>
						<td class="formGrid-title">Oracle Table</td>
						<td class="formGrid-content">
							<input type="text" id="oracleTable">
						</td>
						<td class="formGrid-title">Owner</td>
						<td class="formGrid-content">
							<input type="text" id="owner">
						</td>
					</tr>
				</table>
				<div class="condition-button-block">
					<cathaybk:Button id="btn_querySta" className="ml-15" content="查詢" isShow="true" />
				</div>
			</fieldset>
			<fieldset id="condition">
				<legend>比對條件</legend>
				<div style="clear:both">
					<table class="grid mt-15" id="tableChoose"></table>
				</div>
				<div class="condition-button-block">
					<cathaybk:Button id="compare" content="比對" isShow="true"/>
				</div>				
			</fieldset>
			<fieldset id="showCompareTable">
				<legend>比對結果</legend>
				<div style="float:right;">
					<cathaybk:Button id="btn_export" className="ml-15 mb-15" content="BTN_EXPORT_EXCEL" isShow="true" ></cathaybk:Button>
				</div>
				<table class="formGrid">			
					<tr>
						<td class="formGrid-title">全部</td>
						<td class="formGrid-content">
							<span id="all"></span>
						</td>
						<td class="formGrid-title">正常</td>
						<td class="formGrid-content">
							<span id="normal"></span>
						</td>
						<td class="formGrid-title">異常</td>
						<td class="formGrid-content">
							<span id="unusual"></span>
						</td>
					</tr>
				</table>
				<div style="clear:both">
					<table class="grid mt-15" id="tableResult"></table>
				</div>
			</fieldset>
		</div>
	</article>
	<%@ include file="/html/cathaybk/common/skeleton-footer.jsp"%>
</body>
</html>