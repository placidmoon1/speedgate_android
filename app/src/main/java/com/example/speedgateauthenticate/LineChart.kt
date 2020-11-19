package com.example.speedgateauthenticate

import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

//number of entries to receive (max)
const val numEntries = 6
//user to reference
const val userID = "user_1"


fun createChart(lineChartView :  MutableList<LineChart>) {
    val TAG = "LineChart.kt"

    //get reference of the firebase database
    val database = Firebase.database

    //limit number of past data entries to retrieve
    val myRef = database.getReference("user/${userID}/past_data").limitToLast(numEntries)

    //place to store retrieved past data
    val past_data_time = mutableListOf<String>()
    val past_data = mutableListOf<Map<String, Float>>()

    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            past_data_time.clear()
            past_data.clear()
            for ((i, entrySnapShot) in dataSnapshot.children.withIndex()) {
                val snapshotKey = entrySnapShot.key
                past_data_time.add(snapshotKey.toString())
                val value : Map<String, Float> = entrySnapShot.value as Map<String, Float>
                past_data.add(value)
            }
            Log.d(TAG, "Key is: $past_data_time")
            Log.d(TAG, "Value is: $past_data")

            var ind : Int = 0
            for (lineChart in lineChartView) {
                val entries = addEntries(past_data_time, past_data, ind)
                val setComp1 = LineDataSet(entries, "Company 1")
                setComp1.setColor(R.color.colorPrimaryDark)
                setComp1.setCircleColor(R.color.colorPrimaryDark)
                setComp1.setDrawValues(false);
                setComp1.axisDependency = YAxis.AxisDependency.LEFT
                val dataSets: MutableList<ILineDataSet> = ArrayList()
                dataSets.add(setComp1)
                val data = LineData(dataSets)
                lineChart.data = data

                //disable xAxis, yAxis, description, legend, touch
                val xAxis: XAxis = lineChart.xAxis
                xAxis.isEnabled = false
                val yAxisLeft: YAxis = lineChart.axisLeft
                yAxisLeft.isEnabled = false
                val yAxisRight: YAxis = lineChart.axisRight
                yAxisRight.isEnabled = false
                val description: Description = lineChart.description
                description.isEnabled = false
                val legend: Legend = lineChart.legend
                legend.isEnabled = false
                lineChart.setTouchEnabled(false)

                lineChart.invalidate()
                ind++
            }
        }
        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    })

}


/**
 * Add entries retrieved from Firebase
 * @param   past_data_time  Time stamp of retrieved data
 * @param   past_data       List of Arrays of floats of measured data
 * @param   ind             Index to reference for past_data (measurement to reference (i.e. dust))
 *
 */
fun addEntries( past_data_time : MutableList<String>,
                past_data : MutableList<Map<String, Float>>,
                ind : Int) : ArrayList<Entry>
{
    val entries = ArrayList<Entry>()
    val order = listOf("cur_temp", "fine_dust", "humidity", "sound", "temperature", "weight")
    for (i in 0 until past_data_time.size ) {
        val anEntry = Entry(i.toFloat(), past_data[i].getValue(order[ind]))
        entries.add(anEntry)
    }
    return entries
}

/**
 * @param  n    number of entries to create
 * Create an arrayList of @param numEntries
 * And add a designated number of random entries
 */
fun addRandomEntries(n: Int): ArrayList<Entry> {
    val entries = ArrayList<Entry>()
    val yVal = FloatArray(n)
    for (i in 0 until n) yVal[i] = (0..6).random().toFloat()
    for (i in 0 until n) {
        val anEntry = Entry(i.toFloat(), yVal[i])
        entries.add(anEntry)
    }
    return entries
}