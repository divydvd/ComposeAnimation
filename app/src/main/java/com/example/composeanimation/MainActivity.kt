package com.example.composeanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeanimation.ui.theme.AnimationLearningGuide
import com.example.composeanimation.ui.theme.AnimationTesting
import com.example.composeanimation.ui.theme.ComplexAnimationExamples
import com.example.composeanimation.ui.theme.ComposeAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAnimationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Switch between different animation screens:

                    // 1. Basic Learning Guide - Individual animation APIs
//                     AnimationLearningGuide(modifier = Modifier.padding(innerPadding))

                    // 2. Complex Examples - Combined animations (RECOMMENDED FOR INTERVIEW)
                    ComplexAnimationExamples(modifier = Modifier.padding(innerPadding))

                    // 3. Your Original Practice File
                    // AnimationTesting(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeAnimationTheme {
        Greeting("Android")
    }
}