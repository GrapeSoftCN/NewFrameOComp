package interfaceApplication;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.jGrapeFW_Message;
import common.java.apps.appsProxy;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.nlogger.nlogger;
import common.java.string.StringHelper;

public class personal {
	
	private GrapeTreeDBModel personal;
	private String pkString;
	
	public personal(){
		
		personal = new GrapeTreeDBModel();
		
		GrapeDBDescriptionModel grapeDBDescriptionModel = new GrapeDBDescriptionModel();
		grapeDBDescriptionModel.importDescription(appsProxy.tableConfig("personal"));
		personal.descriptionModel(grapeDBDescriptionModel);

		GrapePermissionsModel grapePermissionsModel = new GrapePermissionsModel();
		grapePermissionsModel.importDescription(appsProxy.tableConfig("personal"));
		personal.permissionsModel(grapePermissionsModel);
		
		pkString = personal.getPk();
		
		personal.checkMode();

	}
	// 新增人员信息
	public String PersonAdd(String Info) {
		int code = 99;
		JSONObject object = JSONObject.toJSON(Info);
		if (object != null) {
			try {
				code=	personal.data(object).insertOnce() instanceof JSONObject ?0:99;
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}
		return resultmessage(code, "人员信息新增成功");
	}

	// 删除人员信息 TODO
	public String PersonDelete(String _id) {
		int code = 99;
		try {
			if(StringHelper.InvaildString(_id)){
				return resultmessage(99, "人员信息为空");
			}
			code =	personal.eq(pkString,_id).delete() instanceof Object ? 0:99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}
		return resultmessage(code, "人员信息删除成功");
	}

	// 批量删除人员信息 TODO
	public String PersonBatchDelete(String ids) {
		int code =99;
		if(StringHelper.InvaildString(ids)){
			return resultmessage(code, "人员信息为空");
		}
		String[] value = ids.split(",");
		try {
			for (String string : value) {
//				personal.eq(pkString, value);
				personal.eq(pkString, string);
			}
			code = personal.deleteAll() == value.length ? 0 : 99;
		} catch (Exception e) {
			nlogger.logout(e);
			code = 99;
		}

		return resultmessage(code, "人员信息批量删除成功");
	}

	// 分页
	@SuppressWarnings("unchecked")
	public String PersonPage(int ids, int pageSize) {
		JSONObject object = new JSONObject();
		JSONArray array = null;
		try {
			array = new JSONArray();
			array =personal.page(ids, pageSize);
		} catch (Exception e) {
			nlogger.logout(e);
			array = new JSONArray();
		} finally {
			object.put("totalsize", (int) Math.ceil((double) array.size() / pageSize));
			object.put("pageSize", pageSize);
			object.put("currentPage", ids);
			object.put("data", array);
		}
	//	return resultMessage(object);


		String info = null;
		try {
			info = appsProxy.proxyCall("/GrapeUser/user/UserPage/int:" + ids + "/int:" + pageSize).toString();
		} catch (Exception e) {
			nlogger.logout(e);
			info = null;
		}
		return info != null ? info : resultmessage(0, "");
	}

	// 按条件分页（条件格式 [k:v]）
	public String PersonPageBy(int ids, int pageSize, String json) {
		String info = null;
		if (JSONObject.toJSON(json) != null) {
			try {
				info = appsProxy.proxyCall("/GrapeUser/user/UserPageBy/int:" + ids + "/int:" + pageSize + "/s:" + json).toString();
			} catch (Exception e) {
				nlogger.logout(e);
				info = null;
			}
		}
		return info != null ? info : resultmessage(0, "");
	}

	// 修改人员信息
	public String PersonUpdate(String id, String info) {
		int code = 99;
		if (JSONObject.toJSON(info) != null) {
			try {
				String msg = appsProxy
						.proxyCall("/GrapeUser/user/UserEdit/s:" + id + "/s:" + info, null, "")
						.toString();
				long codes = Long.parseLong(JSONObject.toJSON(msg).get("errorcode").toString());;
				code = Integer.parseInt(String.valueOf(codes));
			} catch (Exception e) {
				nlogger.logout(e);
				code = 99;
			}
		}
		return resultmessage(code, "人员信息修改成功");
	}

	private String resultmessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
