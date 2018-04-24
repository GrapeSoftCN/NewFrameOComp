package interfaceApplication;


import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.rMsg;
import common.java.apps.appsProxy;
import common.java.check.checkHelper;
import common.java.database.dbFilter;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.string.StringHelper;

public class Ognization {
	private GrapeTreeDBModel _content = new GrapeTreeDBModel();
	private String pkString;
	
	public Ognization() {
		//数据模型
		GrapeDBDescriptionModel _gDbSpecField = new GrapeDBDescriptionModel();
		_gDbSpecField.importDescription(appsProxy.tableConfig("Ognization"));
		_content.descriptionModel(_gDbSpecField);
		
		//权限模型
		GrapePermissionsModel grapePermissionsModel = new GrapePermissionsModel();
		grapePermissionsModel.importDescription(appsProxy.tableConfig("Ognization"));
		_content.permissionsModel(grapePermissionsModel);
		
		pkString = _content.getPk();
		
		//开启校验
		_content.checkMode();
	}
	
	// 新增组织机构信息,返回新增的组织结构信息
	public String OrganAdd(String Info) {
		//String info = appsProxy.proxyCall("/GrapeUser/roles/RoleInsert/" + Info)
		//		.toString();
		if(StringHelper.InvaildString(Info)){
			return rMsg.netMSG(1, "新增组织信息不能为空");
		}
		JSONObject json = JSONObject.toJSON(Info);
		Object  info = _content.data(json).insertOnce();
		return(info != null) ? rMsg.netMSG(0, "新增组织机构成功") :  rMsg.netMSG(1, "新增组织失敗");
	}
	
	@SuppressWarnings("unused")
	private String find(String oid) {
		String _id = null;
		JSONObject object = _content.eq("oid", oid).field(pkString).find();
		if (object != null && object.size() > 0) {
			_id = object.getString(pkString);
		}
		return _id;
	}
	
	// 删除组织机构信息
	public String OrganDelete(String _id) {
		JSONObject object = null;
		if (!StringHelper.InvaildString(_id)) {
			object = _content.eq(pkString, _id).delete();
		}
		String result = rMsg.netMSG(1, "组织机构删除失敗");
		return (object != null) ? rMsg.netMSG(0, "组织机构删除成功") : result;
	}

	// 批量删除组织机构信息
	public String OrganBatchDelete(String ids) {
		long code = 0;
		dbFilter filter = new dbFilter();
		String[] value = null;
		String result = rMsg.netMSG(100, "组织机构批量删除失败");
		if (!StringHelper.InvaildString(ids)) {//TODO 1
			value = ids.split(",");
		}
		if (value != null) {
			for (String id : value) {
				if (!StringHelper.InvaildString(id)) {//TODO 1
					if (ObjectId.isValid(id) || checkHelper.isInt(id)) {
						filter.eq(pkString, id);
					}
				}
			}
			JSONArray condArray = filter.build();
			if (condArray != null && condArray.size() > 0) {
				code = _content.or().where(condArray).deleteAll();
				result = code >= 0 ? rMsg.netMSG(0, "组织机构批量删除成功") : result;
			}
		}
		return result;
	}

	// 分页
	public String OrganPage(int ids, int pageSize) {
		 return OrganPageBy(ids, pageSize, null);
	}


	// 按条件分页（条件格式 [k:v]）
	public String OrganPageBy(int ids, int pageSize, String json) {
		JSONArray CondArray = null;
		JSONArray array = null;
		long total = 0;
		if (StringHelper.InvaildString(json)) {
			CondArray = (CondArray == null || CondArray.size() <= 0) ? JSONArray.toJSONArray(json) : CondArray;
			if (CondArray != null && CondArray.size() >= 0) {
				_content.where(CondArray);
			}
		}
		array = _content.dirty().page(ids, pageSize);
		total = _content.count();
		return rMsg.netPAGE(ids, pageSize, total, array);
		//String info = null;
	//	if (JSONObject.toJSON(json) != null) {
	//		try {
	//			info = appsProxy.proxyCall("/GrapeUser/16/roles/RolePageBy/int:" + ids + "/int:" + pageSize + "/s:" + json).toString();
	//		} catch (Exception e) {
	//			nlogger.logout(e);
	//			info = null;
	//		}
	//	}
	//	return info != null ? info : resultmessage(0, info);
	}

	// 修改组织机构信息
	public String OrganUpdate(String id, String info) {
		JSONObject json = JSONObject.toJSON(info);
		Object obj=null;
		if (StringHelper.InvaildString(id) && info != null && json.size() > 0) {
		 obj =	_content.eq(pkString, id).data(json).updateEx();
		}
		//int code = 99;
		//if (JSONObject.toJSON(info) != null) {
		//	try {
		//		String msg = appsProxy.proxyCall("/GrapeUser/16/roles/RoleUpdate/s:" + id + "/s:" + info).toString();
		//		long codes = Long.parseLong(JSONObject.toJSON(msg).get("errorcode").toString());
		//		code = Integer.parseInt(String.valueOf(codes));
		//	} catch (Exception e) {
		//		nlogger.logout(e);
		//		code = 99;
		//	}
		//}
		String result = rMsg.netMSG(1, "组织机构删除失敗");
		return (obj != null) ? rMsg.netMSG(0, "组织机构删除成功") : result;

	}

	// 设置上级机构
	//TODO 
	@SuppressWarnings("unchecked")
	public String OgSetFatherid(String id, String fatherid) {
		JSONObject json =null;
			Object obj = _content.eq(pkString,id).eq("fatherid",fatherid).find();
			if(null!=obj) {
				json = JSONObject.toJSON(obj.toString());
				json.put("id",id);
				json.put("fatherid",fatherid);
			}
			Object upObj = _content.data(json).updateEx();

		String result = rMsg.netMSG(1, "组织机构修改失敗");
		 return (upObj != null) ? rMsg.netMSG(0, "组织机构修改成功") : result;
	}

}
