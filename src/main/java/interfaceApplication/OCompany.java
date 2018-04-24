package interfaceApplication;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.java.JGrapeSystem.jGrapeFW_Message;
import common.java.apps.appsProxy;
import common.java.interfaceModel.GrapeDBDescriptionModel;
import common.java.interfaceModel.GrapePermissionsModel;
import common.java.interfaceModel.GrapeTreeDBModel;
import common.java.nlogger.nlogger;

public class OCompany {
	
	private GrapeTreeDBModel _content = new GrapeTreeDBModel();
	private JSONObject _obj = new JSONObject();
	private String pkString;
	
	public OCompany() {
		
		//数据模型
        GrapeDBDescriptionModel _gDbSpecField = new GrapeDBDescriptionModel();
        _gDbSpecField.importDescription(appsProxy.tableConfig("Ognization"));
        _content.descriptionModel(_gDbSpecField);
        
        //权限模型
        GrapePermissionsModel grapePermissionsModel = new GrapePermissionsModel();
        grapePermissionsModel.importDescription(appsProxy.tableConfig("Ognization"));
        _content.permissionsModel(grapePermissionsModel);
        
        pkString = _content.getPk();
        
        //开启
        _content.checkMode();
	}

	// 分页
	public String OCompPage(int ids, int pageSize) {
		return page(ids, pageSize);
	}

	// 按条件分页（条件格式 [k:v]）
	public String OCompPageBy(int ids, int pageSize, String json) {
		return page(ids, pageSize, JSONObject.toJSON(json));
	}

	// 修改运行单位信息
	public String OCompUpdate(String id, String info) {
		return resultMessage(update(id, JSONObject.toJSON(info)), "修改成功");
	}
	
	/**
	 * 修改运行单位信息
	 * 
	 * @param OCID
	 * @param object
	 * @return
	 */
	public int update(String OCID, JSONObject object) {
		int code = 99;
		if (object != null) {
			// 修改基本信息
			if (object.containsKey("ownid")) {
				object.remove("ownid");
			}
			code = _content.eq(pkString, new ObjectId(OCID)).data(object).updateEx() ? 0 : 99;
		}
		return code;
	}
	
	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			JSONArray array = _content.page(idx, pageSize);
			object.put("totalSize", (int) Math.ceil((double) _content.count() / pageSize));
			object.put("currentPage", idx);
			object.put("pageSize", pageSize);
			object.put("data", array);
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize, JSONObject object) {
		JSONObject _obj = null;
		if (object != null) {
			try {
				for (Object object2 : object.keySet()) {
					if (object.containsKey(pkString)) {
						_content.eq(pkString, new ObjectId(object.get(pkString).toString()));
					}
					_content.eq(object2.toString(), object.get(object2.toString()));
				}
				JSONArray array = _content.dirty().page(idx, pageSize);
				_obj = new JSONObject();
				_obj.put("totalSize", (int) Math.ceil((double) _content.count() / pageSize));
				_obj.put("currentPage", idx);
				_obj.put("pageSize", pageSize);
				_obj.put("data", array);
			} catch (Exception e) {
				nlogger.logout(e);
				_obj = null;
			}finally {
				_content.clear();
			}
		}
		return resultMessage(_obj);
	}
	
	public String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填项没有填";
			break;
		case 2:
			msg = "没有修改数据权限，请联系管理员进行权限调整";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
	
	@SuppressWarnings("unchecked")
	private String resultMessage(JSONObject object) {
		if (object == null) {
			object = new JSONObject();
		}
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}
	
}
