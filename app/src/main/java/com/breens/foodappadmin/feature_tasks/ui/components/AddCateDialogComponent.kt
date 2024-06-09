package com.breens.foodappadmin.feature_tasks.ui.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.breens.foodappadmin.feature_tasks.state.CatesScreenUiState
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCateDialogComponent(

    setCateImage:(String)->Unit,
    setCateTitle: (String) -> Unit,
    saveCate: () -> Unit,
    uiStateCate: CatesScreenUiState,
    closeDialog: () -> Unit,

) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uiStateCate.bitmapCate = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                var source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }

    }
    Dialog(onDismissRequest = { closeDialog() }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn(contentPadding = PaddingValues(12.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "New Task",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(12.dp))
                    uiStateCate.bitmapCate. let {bitmap->
                        if(bitmap!=null){
                            uploadCateImageCate(bitmap, context as ComponentActivity){ success, imageUrl ->
                                if(success){
                                    imageUrl.let{
                                        uiStateCate.imgUrlCate= it

                                    }
                                    setCateImage(uiStateCate.imgUrlCate)
                                }

                            }
                        }
                    }

                    if(uiStateCate.bitmapCate!=null){
                        Text(text = uiStateCate.imgUrlCate, color = Color.Yellow)
                        Image(
                            bitmap = uiStateCate.bitmapCate?.asImageBitmap()!!,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(160.dp)
                                .height(170.dp)
                        )
                    }
                    TextButton(
                        onClick = {

                            launcher.launch("image/*")
                        },
                        modifier = Modifier
                            .border(BorderStroke(2.dp, Color.Black))
                    ) {
                        Text(
                            text = "Gallery",
                        )

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiStateCate.currentTextFieldTitleCate,
                        onValueChange = { title ->
                            setCateTitle(title)
                        },
                        label = { Text("Task Title") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                        ),
                    )
                }



                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                saveCate()
                                uiStateCate.bitmapCate = null
                                setCateTitle("")
                                closeDialog()
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(text = "Save Task")
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                closeDialog()
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(text = "Đóng")
                        }
                    }
                }
            }
        }
    }
}
fun uploadCateImageCate(bitmap2: Bitmap, context: ComponentActivity, calBack: (Boolean, String)->Unit) {
    val storageRef = Firebase.storage.reference
    val imageRef2 = storageRef.child("categories/${bitmap2}")

    val baos2 = ByteArrayOutputStream()
    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos2)
    val imageData2 = baos2.toByteArray()

    imageRef2.putBytes(imageData2).addOnSuccessListener {
        imageRef2.downloadUrl.addOnSuccessListener { uri2->
            val imageUrl2 = uri2.toString()
            calBack(true, imageUrl2)

        }.addOnFailureListener {
            calBack(false, null.toString())
        }
    }.addOnFailureListener {
        calBack(false, null.toString())
    }
}
