package ${PACKAGE_NAME}

import android.content.Context
#set( $numc = 1)
#if($numeroColumnas <= $numc)
import android.support.v7.widget.LinearLayoutManager
#else
import android.support.v7.widget.GridLayoutManager
#end
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/* pegar el siguiente codigo descomentariado en el activity o fragment
 //------------------manejador recycler

    private fun managerReciclerView(){
        rv_list.adapter = null
        if (listVacio.isEmpty())
        {
            emptyList()
            return
        }
        fillList()
        ${NAME}(
                this,
                listVacio,
                //Recycler a llenar
                $nombre_recycler,
                true,
                ::itemSelected$modelo,
                ::itemEdit$modelo,
                ::itemDelete$modelo
        )
    }

    fun itemSelected$modelo(obj: $modelo){
        Log.e("","")
    }

    fun itemDelete$modelo(obj: $modelo){
        Log.e("","")
    }

    fun itemEdit$modelo(obj: $modelo){
        Log.e("","")
    }

    fun emptyList(){

    }

    fun fillList(){

    }
//---------------------fin manejador recycler
*/

class ${NAME} (
        val context         :   Context,
        val list$modelo     :   MutableList<$modelo>,
        val $nombre_recycler:   RecyclerView,
        val enableEdit      :   Boolean,
        val itemSelected    :   (($modelo)->Unit) ?= null,
        val editItem        :   (($modelo)->Unit) ?= null,
        val deleteItem      :   (($modelo)->Unit) ?= null
):RecyclerView.Adapter<${NAME}.Item$modelo>(){
    init {
        setAdapter()
    }

    private fun setAdapter(){
        if (${nombre_recycler}.adapter != null){
            return
        }
        #if($numeroColumnas <= $numc)
        ${nombre_recycler}.layoutManager = LinearLayoutManager(context)
        #else
        ${nombre_recycler}.layoutManager = GridLayoutManager(context,$numeroColumnas)
        #end
        ${nombre_recycler}.adapter =  this
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Item$modelo {
        val view=LayoutInflater.from(context).inflate(R.layout.$nombre_layout,null,false)
        val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT)
        view.layoutParams=params
        return Item${modelo}(view)
    }

    override fun getItemCount(): Int {
        return list${modelo}.size
    }

    override fun onBindViewHolder(p0: Item$modelo, p1: Int) {
        p0.fillView(list${modelo}.get(p1),p1)
    }

    inner class Item$modelo(val view: View):RecyclerView.ViewHolder(view){
        private val tv_text     : TextView  = view.findViewById(R.id.tv_text)
        private val iv_delete   : ImageView = view.findViewById(R.id.iv_delete)
        private val iv_edit     : ImageView = view.findViewById(R.id.iv_edit)
        private var $modelo     : $modelo    ?= null
        init {
            litenersView()
            disabledView()
        }
        
        fun disabledView(){
            if (enableEdit)return
            tv_text.isEnabled =false
            iv_delete   .isEnabled =false
            iv_edit     .isEnabled =false
        }

        fun litenersView(){
            tv_text.setOnClickListener {
                itemSelected?.invoke($modelo!!)
            }
            iv_edit.setOnClickListener {
                editItem?.invoke($modelo!!)
            }
            iv_delete.setOnClickListener {
                deleteItem?.invoke($modelo!!)
            }
        }

        fun fillView($modelo: $modelo,position :Int){
            this.$modelo = $modelo
            // llena vista
        }
    }
}
