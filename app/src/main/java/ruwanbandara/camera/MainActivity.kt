package ruwanbandara.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


private val PackageManager.PERMISSION_DENIED: Any?
    get() {
        return -1;
    }

class MainActivity : AppCompatActivity(), View.OnClickListener {


    private val IMAGE_CPATURE_CODE = 1001
    private val PERMISSION_CODE =1000
    var image_rui: Uri? = null

    // firebase connecting
    private var filepath: Uri? = null
    internal var storage:FirebaseStorage? = null
    internal var storageReferences: StorageReference? = null


    override fun onClick(p0: View){
        if (p0 == capture_btn)
            openCamera()
        else if (p0 == Upload_btn)
            uploadFile()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //button click
        capture_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    == packageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == packageManager.PERMISSION_DENIED){

                    //permission was not enabled

                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    // show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    openCamera()

                }

            }
            else{
                //system os is < marshmallow
                openCamera()
            }

        }

        // Upload button
        Upload_btn.setOnClickListener{


        }

        //Init Firebase
        storage = FirebaseStorage.getInstance()
        storageReferences = storage!!.reference


    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_rui = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        println("test")
        println(image_rui)
        // camera intent

        filepath = image_rui

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
        startActivityForResult(cameraIntent, IMAGE_CPATURE_CODE)

    }
    private fun uploadFile(){
        if (filepath != null){

            val ProgressDialog = ProgressDialog(this)
            ProgressDialog.setTitle("Uploading....")
            ProgressDialog.show()

            val imageRef = storageReferences!!.child("images/"+ UUID.randomUUID().toString())
            imageRef.putFile(filepath!!)
                .addOnSuccessListener {
                    ProgressDialog.dismiss()
                    Toast.makeText(applicationContext,"File Upload",Toast.LENGTH_SHORT).show()
                    
                }
                .addOnFailureListener {
                    ProgressDialog.dismiss()
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()

                }
                .addOnProgressListener{taskSnapshot ->
                    val progress =100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    ProgressDialog.setMessage("uploaded" +progress.toInt()+"....")
            }
        }
    }

    @SuppressLint("WrongConstant")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)


        // called when user presses ALLOw or DENT frompermission requs popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED){
                    // permission from popup was granted
                    openCamera()
                }
                else{
                    // permission from popup was denied
                    Toast.makeText(this,"Permission denied", 1).show()

                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image captured form camera intent
        if (resultCode == Activity.RESULT_OK){
            //set image cpatured to image viwe
        }
    }
}
