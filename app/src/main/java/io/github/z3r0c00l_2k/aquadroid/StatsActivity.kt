package io.github.z3r0c00l_2k.aquadroid

import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.github.z3r0c00l_2k.aquadroid.helpers.SqliteHelper
import io.github.z3r0c00l_2k.aquadroid.utils.AppUtils
import io.github.z3r0c00l_2k.aquadroid.utils.ChartXValueFormatter
import kotlinx.android.synthetic.main.activity_stats.*
import java.math.RoundingMode
import java.text.DecimalFormat


class StatsActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private var totalPercentage: Float = 0f
    private var totalGlasses: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        sqliteHelper = SqliteHelper(this)

        btnBack.setOnClickListener {
            finish()
        }

        greetings.text = getString(R.string.hello_text) + sharedPref.getString(
            AppUtils.NAME_KEY,
            "User"
        ) + getString(R.string.water_habit_text)

        val entries = ArrayList<Entry>()
        val dateArray = ArrayList<String>()

        val cursor: Cursor = sqliteHelper.getAllStats()

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                dateArray.add(cursor.getString(1))
                val percent = cursor.getInt(2) / cursor.getInt(3).toFloat() * 100
                totalPercentage += percent
                totalGlasses += cursor.getInt(2)
                entries.add(Entry(i.toFloat(), percent))
                cursor.moveToNext()
            }

        } else {
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }

        if (!entries.isEmpty()) {

            chart.description.isEnabled = false
            chart.animateY(1000, Easing.Linear)
            chart.viewPortHandler.setMaximumScaleX(1.5f)
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.position = XAxis.XAxisPosition.TOP
            chart.xAxis.isGranularityEnabled = true
            chart.legend.isEnabled = false
            chart.fitScreen()
            chart.isAutoScaleMinMaxEnabled = true
            chart.scaleX = 1f
            chart.setPinchZoom(true)
            chart.isScaleXEnabled = true
            chart.isScaleYEnabled = false
            chart.axisLeft.textColor = Color.WHITE
            chart.xAxis.textColor = Color.WHITE
            chart.axisLeft.setDrawAxisLine(false)
            chart.xAxis.setDrawAxisLine(false)
            chart.setDrawMarkers(false)
            val rightAxix = chart.axisRight
            rightAxix.setDrawGridLines(false)
            rightAxix.setDrawZeroLine(false)
            rightAxix.setDrawAxisLine(false)
            rightAxix.setDrawLabels(false)

            val dataSet = LineDataSet(entries, "Label")
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 0f
            dataSet.setDrawFilled(true)
            dataSet.setDrawValues(false)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

            val lineData = LineData(dataSet)
            chart.xAxis.valueFormatter = (ChartXValueFormatter(dateArray))
            chart.data = lineData
            chart.invalidate()
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            totalIntakePercentage.text = "" + df.format((totalPercentage / cursor.count)) + " %"
            avgGlassPD.text = getString(R.string.average_glasses_day_text) + df.format((totalGlasses / cursor.count))

        }

    }
}
