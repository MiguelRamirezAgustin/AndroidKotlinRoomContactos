package com.ejemplo.projectdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.projectdb.db.ContacModel
import com.ejemplo.projectdb.db.Contact
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_toast.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: AdapterContact
    var listContact : MutableList<ContacModel>  = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()


        //Agregar nuevo contacto
        btnAddUser.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }
        getContact()

        //evento para ordenar por nombre
        btn_order.setOnClickListener {
            doAsync {
                val listContactdb = Contact.dbcontact.contacDao().getContactOrder()
                uiThread {
                        listContact.clear()
                        text_title.visibility = View.GONE
                        for (i in 0 until listContactdb.size) {
                            val contact = ContacModel(
                                listContactdb[i].id,
                                listContactdb[i].name,
                                listContactdb[i].lastname,
                                listContactdb[i].motherLastName,
                                listContactdb[i].phone,
                                listContactdb[i].imagen
                            )
                            listContact.add(contact)
                        }
                    onComplete {
                        println("Lista de contactos order ----"+listContact)
                        toasShow("Lista ordenada corectamente")
                        adapter = AdapterContact(listContact,this@MainActivity, {deleteItem(it)})
                        recyclerView = findViewById(R.id.rv_contact)
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        recyclerView.adapter = adapter
                    }
                }
            }
        }
    }


    //consulta de items de db
    private fun getContact() {
        doAsync {
            val listContactdb = Contact.dbcontact.contacDao().getContact()
            uiThread {
                Log.d("TAg", "ListaContact total " +listContactdb.size )
                listContact.clear()
                if(listContactdb.size == 0 ){
                    text_title.visibility = View.VISIBLE
                    btn_order.visibility = View.GONE
                }else{
                    text_title.visibility = View.GONE
                    for (i in 0 until listContactdb.size) {
                        var item = listContactdb[i]
                        println("item ----"+item)
                        val contact = ContacModel(
                            listContactdb[i].id,
                            listContactdb[i].name,
                            listContactdb[i].lastname,
                            listContactdb[i].motherLastName,
                            listContactdb[i].phone,
                            listContactdb[i].imagen
                        )
                        listContact.add(contact)
                    }
                }
                onComplete {
                    println("Lista de contactos ----"+listContact)
                    adapter = AdapterContact(listContact,this@MainActivity, {deleteItem(it)})
                    recyclerView = findViewById(R.id.rv_contact)
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerView.adapter = adapter
                }
            }
        }
    }


    //Elimina items de contacto
    fun deleteItem(contact: ContacModel){
        doAsync {
            val idItem = contact.id
            println("Id Eliminar "+ idItem +" Item seleccionado "+contact)
            Contact.dbcontact.contacDao().deleteItemContact(idItem!!.toInt())
            uiThread {
                getContact()
            }
        }
    }

    // mensaje toas custom
    private fun toasShow(message:String){
        val customToastLayout = layoutInflater.inflate(R.layout.custom_toast,ly_toasCustom)
        val textView = customToastLayout.findViewById<TextView>(R.id.tv_toast)
        textView.setText(message)
        val customToast = Toast(this)
        customToast.view = customToastLayout
        customToast.setGravity(Gravity.CENTER,0,0)
        customToast.duration = Toast.LENGTH_SHORT
        customToast.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}