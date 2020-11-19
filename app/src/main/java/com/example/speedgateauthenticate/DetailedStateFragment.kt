package com.example.speedgateauthenticate

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailedStateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailedStateFragment : Fragment() {
    private val TAG = "DetailedStateFragment"
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thisView : View? = inflater.inflate(R.layout.fragment_detailed_state, container, false)

        //line charts
        val lineChartView : MutableList<LineChart> = mutableListOf()
        lineChartView.add(thisView!!.findViewById<LineChart>(R.id.env_temp_line_chart))
        lineChartView.add(thisView.findViewById<LineChart>(R.id.dust_line_chart))
        lineChartView.add(thisView.findViewById<LineChart>(R.id.env_humidity_line_chart))
        lineChartView.add(thisView.findViewById<LineChart>(R.id.noise_line_chart))
        lineChartView.add(thisView.findViewById<LineChart>(R.id.temp_line_chart))
        lineChartView.add(thisView.findViewById<LineChart>(R.id.weight_line_chart))

        //initialize data for charts
        createChart(lineChartView)

        //cardValues
        val cardValues : MutableList<TextView> = mutableListOf()
        cardValues.add(thisView.findViewById(R.id.env_temp_card_value))
        cardValues.add(thisView.findViewById(R.id.dust_card_value))
        cardValues.add(thisView.findViewById(R.id.env_humidity_card_value))
        cardValues.add(thisView.findViewById(R.id.noise_card_value))
        cardValues.add(thisView.findViewById(R.id.temp_card_value))
        cardValues.add(thisView.findViewById(R.id.weight_card_value))

        //change cardValues appropriately
        setCardValues(cardValues)

        return thisView
    }

    private fun setCardValues(cardValues: MutableList<TextView>) {
        val database = Firebase.database
        val myRef = database.getReference("user/${userID}/current_data")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue<Map<String, Float>>()
                val order = listOf("cur_temp", "fine_dust", "humidity", "sound", "temperature", "weight")
                for (i in 0 until 6) { //[0, 5]
                        cardValues[i].text = post!!.getValue(order[i]).toString()
                }
                Log.d(TAG, post.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())            }
        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailedStateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailedStateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}