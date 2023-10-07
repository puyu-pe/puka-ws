package pe.puyu.service.bifrost;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class BifrostResponse {
  private Map<String, JSONObject> data;
  private String message;
  private String status;

  public BifrostResponse(JSONObject response) throws JSONException {
    data = new LinkedHashMap<>();
    if (response.has("message")) {
      message = response.getString("message");
    }
    if (response.has("status")) {
      status = response.getString("status");
    }
    if(response.has("data")){
      initializeData(response.getJSONObject("data"));
    }
  }

  private void initializeData(JSONObject jsonData) throws JSONException {
    var keys = jsonData.keys();
    while (keys.hasNext()) {
      var itemId = keys.next().toString();
      var value = new JSONObject(jsonData.getString(itemId));
      data.put(itemId, value);
    }
  }

  public Map<String, JSONObject> getData() {
    return data;
  }

  public String getMessage() {
    return message;
  }

  public String getStatus() {
    return status;
  }

}
