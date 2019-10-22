package ruwanbandara.camera

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


private val Any.PERMISSION_DENIED: Any?
    get() {return 1.0}

class MainActivity : AppCompatActivity() {

    private val PERMISSION_CODE: Int =1000;

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


    }

    private fun openCamera() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}
