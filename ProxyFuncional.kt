package ${PACKAGE_NAME}

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
/***
 * Poner la libreria en el gradle
 * implementation 'com.google.code.gson:gson:2.8.5'
 * Convierte un JSONArray a un arreglo de objetos
 */
import com.google.gson.Gson
import kotlinx.coroutines.experimental.launch
import org.jetbrains.annotations.NotNull
import org.json.JSONArray
import org.json.JSONObject

class ${NAME}(val context: Context) {
    enum class Metodos(val metodo: Int) {
        GET(Request.Method.GET),
        POST(Request.Method.POST),
        PUT(Request.Method.PUT)
    }

    enum class Rutas(val servidor:String?,val mock:String?, val metodo:Metodos?,val esArreglo:Boolean,val isMock:Boolean){
        BASE(null,"http://demo1086856.mockable.io/",null,false,false),
        POSICION(null,"posision1",Metodos.GET,true,true),
    }
    //Tiempo en segundos
    private val DURATION_REQUEST_SERVICE: Int = 30
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    fun <T : Any?, K : Any?> realizarSolicitud(
            objeto: T?,
            clase: Class<K>,
            servicio: Rutas,
            produccionListener: IProduccionListener? = null,
            respuestaExitosa: ((Any?) -> Unit)? = null,
            respuestaFallida: ((Any?) -> Unit)? = null
    ) = launch {
        val url:String = if(servicio.isMock){ Rutas.BASE.mock+servicio.mock }else{ Rutas.BASE.servidor+servicio.servidor }
        val funStart :()->Unit
        class ErrorResponse :Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.e("error", Log.getStackTraceString(error))
                if (this@${NAME}.context !is Activity){
                    respuestaFallida?.invoke(null)
                    return
                }
                this@${NAME}.context.runOnUiThread {
                    respuestaFallida?.invoke(null)
                }
            }

        }
        if(!servicio.esArreglo){
            class OKObject:Response.Listener<JSONObject>{
                override fun onResponse(response: JSONObject?) {
                    if(this@${NAME}.context !is Activity){
                        respuestaExitosa?.invoke(JSONObjectToObject(response.toString(),clase))
                        return
                    }
                    this@${NAME}.context.runOnUiThread {
                        respuestaExitosa?.invoke(JSONObjectToObject(response.toString(),clase))
                    }
                }

            }
            funStart ={
                requestQueue.add(JsonObjectRequest(
                        servicio.metodo!!.metodo,
                        url,
                        if(objeto != null) convertirAJSON(objeto) as JSONObject else null,
                        OKObject(),
                        ErrorResponse()
                ))
            }

        }
        else{
            class Accion :Response.Listener<JSONArray>{
                override fun onResponse(response: JSONArray?) {
                    if(this@${NAME}.context !is Activity){
                        respuestaExitosa?.invoke(this@${NAME}.JSONArrayToArrayObject(response.toString(),clase))
                        return
                    }
                    this@${NAME}.context.runOnUiThread {
                        respuestaExitosa?.invoke(this@${NAME}.JSONArrayToArrayObject(response.toString(),clase))
                    }
                }
            }
            funStart = {
                requestQueue.add(JsonArrayRequest(
                        servicio.metodo!!.metodo,
                        url,
                        if(objeto != null){ convertirAJSON(objeto) as JSONArray }else null,
                        Accion(),
                        ErrorResponse()
                ))
            }
        }
        funStart.invoke()
    }

    private fun <K : Any?> JSONArrayToArrayObject(@NotNull json:String,@NotNull clase:Class<K>):MutableList<K>?{
        val jsonArray = JSONArray(json)
        var list :MutableList<K> = emptyList<K>().toMutableList()
        try {
            val gson = Gson()
            for (contador in 0 until jsonArray.length()) {
                val tmp = gson.fromJson(jsonArray[contador].toString(), clase)
                list.add(tmp)
            }
        } catch (e: Exception) {
            Log.e("Error",Log.getStackTraceString(e))
            list = emptyList<K>().toMutableList()
        }
        return list
    }

    /**
     * Convierte un objetojson a un object
     */
    private fun <T:Any?> JSONObjectToObject(@NotNull json:String,@NotNull clase : Class<T>):Any?{
        return try {
            val gson =Gson()
            gson.fromJson(json,clase)
        } catch (e: Exception) {
            Log.e("Error",Log.getStackTraceString(e))
            null
        }
    }

    private fun convertirAJSON(obj:Any?):String?{
        if(obj == null){
            return null
        }
        val gson = Gson()
        return gson.toJson(obj)
    }

}