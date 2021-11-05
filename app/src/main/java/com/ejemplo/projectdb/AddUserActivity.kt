package com.ejemplo.projectdb

import android.R.attr
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_user.*
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import android.view.Gravity
import com.ejemplo.projectdb.db.Contact
import com.ejemplo.projectdb.db.ContactEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.custom_toast.*


class AddUserActivity : AppCompatActivity() {

    lateinit var progreesDialog: ProgressDialog
    private val CAMERA_REQUEST = 100
    private var parameterUpdate:Boolean = false // Parametro para validar la actualizacion
    private var idUpdate:String? = ""
    private var imageCapture:String? = ""
    private var validateChange:Boolean = false // valida alguna modificacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        supportActionBar?.hide()
        // data putExtra edit

        parameterUpdate = intent.getBooleanExtra("paramUpdate", false)
        idUpdate = intent.getStringExtra("_id").toString()
        var name = intent.getStringExtra("_name").toString()
        var lastName = intent.getStringExtra("_lastName").toString()
        var motherLastName = intent.getStringExtra("_motherLastName").toString()
        var phone = intent.getStringExtra("_phone").toString()
        var imagen = intent.getStringExtra("_image").toString()

        println("Parametros --> "+ idUpdate + name + lastName + motherLastName + phone)
        println("Parametro para validar la actualizacion "+ parameterUpdate)

        //Validacion para actualizar datos
        if(parameterUpdate){
            tv_title.setText(R.string.txt_contact_Update)
            btnAddContact.setText("Actualizar contacto")
            editTextName.setText(name)
            editTextLastName.setText(lastName)
            editTexMotherLastName.setText(motherLastName)
            editTextPhone.setText(phone)
            imageCapture = "";
            imageCapture = imagen
            val imageBytes = Base64.decode(imagen, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            image.setImageBitmap(decodedImage)
        }

        //evento para camara
        image.setOnClickListener {
            if (parameterUpdate){
                validateChange = true
            }
            openCamera()
        }

        btnAddContact.setOnClickListener {
            if(parameterUpdate){
               if(!validateChange){
                   toasShow("No ha realizado ninguna actualizacion")
                   return@setOnClickListener
               }
            }

            if(!editTextName.text!!.isNotEmpty()){
                toasShow("Es requerido el nombre")
                return@setOnClickListener
            }
            if(!editTextLastName.text!!.isNotEmpty()){
                toasShow("Es requerido el paterno")
                return@setOnClickListener
            }
            if(!editTexMotherLastName.text!!.isNotEmpty()){
                toasShow("Es requerido el apellido materno")
                return@setOnClickListener
            }
            if(!editTextPhone.text!!.isNotEmpty()){
                toasShow("Es requerido el numero de telefono")
                return@setOnClickListener
            }
            if(editTextPhone.text!!.length <= 9){
                toasShow("Telefono invalido no cuenta con 10 digitos")
                return@setOnClickListener
            }
            if(imageCapture == ""){
                toasShow("Es requerido tomar una foto")
                return@setOnClickListener
            }
            progreesDialog = ProgressDialog(this)
            if(parameterUpdate){
                progreesDialog.setTitle("Actualizando..")
            }else{
                progreesDialog.setTitle("Creando..")
            }
            progreesDialog.setCancelable(false)
            progreesDialog.show()
            var modelContact = ContactEntity(
                0,
                editTextName.text.toString(),
                editTextLastName.text.toString() ,
                editTexMotherLastName.text.toString(),
                editTextPhone.text.toString(),
                imageCapture!!
            )
            // Agregar contacto
            doAsync {
                if(parameterUpdate){
                    Contact.dbcontact.contacDao().updateContact(
                        modelContact.name,
                        modelContact.lastname,
                        modelContact.motherLastName,
                        modelContact.phone,
                        modelContact.imagen,
                        idUpdate!!.toInt()
                    )
                }else{
                    Contact.dbcontact.contacDao().insertContact(modelContact)
                }
                uiThread {
                    progreesDialog.hide()
                    if(parameterUpdate){
                        toasShow("Contacto actualizado correctamente")
                    }else{
                        toasShow("Contacto creado correctamente")
                    }
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        //Valida alguna modificación
        editTextName.doOnTextChanged { inputText, _, _, _ ->
            validateChange = true
        }
        editTextLastName.doOnTextChanged { inputText, _, _, _ ->
            validateChange = true
        }
        editTexMotherLastName.doOnTextChanged { inputText, _, _, _ ->
            validateChange = true
        }
        editTextPhone.doOnTextChanged { inputText, _, _, _ ->
            validateChange = true
        }
    }



    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, CAMERA_REQUEST)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            val bitmapImg = data?.extras?.get("data") as Bitmap
            imageCapture = encodeImage(bitmapImg)
            println("Imagen capturada--> "+ bitmapImg)
            image.setImageBitmap(bitmapImg)
            println("Imagen capturada "+ imageCapture)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return android.util.Base64 .encodeToString(b, android.util.Base64.DEFAULT)
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST)
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}