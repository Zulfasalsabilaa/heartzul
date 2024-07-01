package com.example.heart

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "heart.tflite"

    private lateinit var resultText: TextView
    private lateinit var male: EditText
    private lateinit var age: EditText
    private lateinit var prevalentStroke: EditText
    private lateinit var prevalentHyp: EditText
    private lateinit var diabetes: EditText
    private lateinit var sysBP: EditText
    private lateinit var diaBP: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        male = findViewById(R.id.male)
        age = findViewById(R.id.age)
        prevalentStroke = findViewById(R.id.prevalentStroke)
        prevalentHyp = findViewById(R.id.prevalentHyp)
        diabetes = findViewById(R.id.diabetes)
        sysBP = findViewById(R.id.sysBP)
        diaBP = findViewById(R.id.diaBP)
        diaBP = findViewById(R.id.diaBP)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                male.text.toString(),
                age.text.toString(),
                prevalentStroke.text.toString(),
                prevalentHyp.text.toString(),
                diabetes.text.toString(),
                sysBP.text.toString(),
                diaBP.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Yes"
                }else if (result == 1){
                    resultText.text = "No"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(8)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String): Int{
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
       }
}



