package com.example.diabet

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModel = "gula.tflite"

    private lateinit var resultTextView: TextView
    private lateinit var preg: EditText
    private lateinit var Glucose: EditText
    private lateinit var Blood: EditText
    private lateinit var Skin: EditText
    private lateinit var Insulin: EditText
    private lateinit var Diabetes: EditText
    private lateinit var Age: EditText
    private lateinit var Outcome: EditText
    private lateinit var btnCheck: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        // Set the back button drawable
        val backDrawable = resources.getDrawable(R.drawable.back, null)
        toolbar.setNavigationIcon(backDrawable)

        // Set the navigation click listener
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        resultTextView = findViewById(R.id.txtResult)
        preg = findViewById(R.id.Preg)
        Glucose = findViewById(R.id.Glucose)
        Blood = findViewById(R.id.Blood)
        Skin = findViewById(R.id.Skin)
        Insulin = findViewById(R.id.Insulin)
        Diabetes = findViewById(R.id.Diabetes)
        Age = findViewById(R.id.Age)
        Outcome = findViewById(R.id.Outcome)
        btnCheck = findViewById(R.id.btnCheck)

        btnCheck.setOnClickListener {
            val result = doInference(
                preg.text.toString(),
                Glucose.text.toString(),
                Blood.text.toString(),
                Skin.text.toString(),
                Insulin.text.toString(),
                Diabetes.text.toString(),
                Age.text.toString()
            )

            resultTextView.text = result
        }


        initInterpreter()
    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(4)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(assets, mModel), options)
    }

    private fun doInference(
        input1: String,
        input2: String,
        input3: String,
        input4: String,
        input5: String,
        input6: String,
        input7: String
    ): String {
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloatOrNull() ?: 0f
        inputVal[1] = input2.toFloatOrNull() ?: 0f
        inputVal[2] = input3.toFloatOrNull() ?: 0f
        inputVal[3] = input4.toFloatOrNull() ?: 0f
        inputVal[4] = input5.toFloatOrNull() ?: 0f
        inputVal[5] = input6.toFloatOrNull() ?: 0f
        inputVal[6] = input7.toFloatOrNull() ?: 0f

        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        // Pilih indeks output dengan nilai maksimum
        val predictedClassIndex = output[0].indexOfFirst { it == output[0].maxOrNull() }

        // Ganti indeks dengan hasil yang sesuai dengan kebutuhan Anda
        return when (predictedClassIndex) {
            0 -> "Tidak terkena diabetes"
            1 -> "Terkena diabetes"
            else -> "Unknown"
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
