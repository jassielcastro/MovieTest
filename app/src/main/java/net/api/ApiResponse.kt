package net.api

import org.json.JSONObject
import org.json.JSONTokener

class ApiResponse(response: String) {

    var success: Boolean = false
    var message: String = ""
    var json: JSONObject = JSONObject()

    init {
        try {
            val jsonToken = JSONTokener(response).nextValue()
            if (jsonToken is JSONObject) {
                val jsonResponse = JSONObject(response)

                success = if (jsonResponse.has("success")) {
                    jsonResponse.getBoolean("success")
                } else {
                    json = jsonResponse
                    true
                }

                message = if (jsonResponse.has("status_message")) {
                    jsonResponse.getString("status_message")
                } else {
                    "An error was occurred while processing the response"
                }
            } else {
                message = response
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}