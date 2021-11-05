package com.ejemplo.projectdb

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.projectdb.db.ContacModel
import kotlinx.android.synthetic.main.item_contact.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AdapterContact(
    var items:List<ContacModel>,
    private val context: Context,
    private val deleteContact: (ContacModel) -> Unit
    ):RecyclerView.Adapter<AdapterContact.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contact, parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items.get(position)
        holder.name.text = model.name
        holder.lastName.text = model.lastName
        holder.motherLastName.text = model.motherLastName
        holder.phone.text = model.phone

        val imageBytes = Base64.decode(model.image, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.imagen.setImageBitmap(decodedImage)

        //event edit
        holder.imgEdit.setOnClickListener {
            val intent = Intent(context, AddUserActivity::class.java)
            intent.putExtra("_id", model.id.toString())
            intent.putExtra("_name", model.name.toString())
            intent.putExtra("_lastName", model.lastName.toString())
            intent.putExtra("_motherLastName", model.motherLastName.toString())
            intent.putExtra("_phone", model.phone.toString())
            intent.putExtra("_image", model.image.toString())
            intent.putExtra("paramUpdate", true)
            context.startActivity(intent)
        }

        //evento delete
        holder.imgDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
                .create()
            val view = LayoutInflater.from(context).inflate(R.layout.alert_custom,null)
            val buttonCancel = view.findViewById<Button>(R.id.btn_cancel)
            val buttonAcept = view.findViewById<Button>(R.id.btn_aceptar)
            builder.setView(view)
            buttonCancel.setOnClickListener {
                builder.dismiss()
            }
            buttonAcept.setOnClickListener {
                builder.dismiss()
                deleteContact(model)
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }

        holder.imagen.setOnClickListener {
            Toast.makeText(context, "id: "+model.id +" nombre: "+model.name, Toast.LENGTH_LONG).show()
        }
    }


    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var imagen = view.image_contact
        var name = view.tv_name
        var lastName = view.tv_lastname
        var motherLastName = view.tv_motherLastName
        var phone = view.tv_phone
        var imgEdit = view.img_edit
        var imgDelete = view.img_delete
    }


}