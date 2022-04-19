package com.example.mymaps

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity() {

    private lateinit var userMaps: MutableList<UserMap>
    private lateinit var mapAdapter: MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvMaps = findViewById<RecyclerView>(R.id.rvMaps)
        val fabCreateMap = findViewById<FloatingActionButton>(R.id.fabCreateMap)

        userMaps =generateSampleData().toMutableList()

        rvMaps.layoutManager = LinearLayoutManager(this)

        mapAdapter = MapsAdapter(this, userMaps, object: MapsAdapter.OnClickListener{
            override fun onItemClick(position: Int) {
                Log.i(TAG, "Tapped on position $position")

                val intent = Intent(this@MainActivity, DisplayMapActivity::class.java)
                intent.putExtra(EXTRA_USER_MAP, userMaps[position])
                startActivity(intent)
            }

        })
        rvMaps.adapter = mapAdapter

        fabCreateMap.setOnClickListener{
            Log.i(TAG, "Tap on Fab")
            showAlertDialog()
        }


    }

    private fun showAlertDialog() {

        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map, null)
        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Map Title")
                .setView(mapFormView)
                .setNegativeButton("cancel", null)
                .setPositiveButton("OK",null)
                .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{

            val title = mapFormView.findViewById<EditText>(R.id.etTitle1).text.toString()

            if(title.trim().isEmpty()){
                Toast.makeText(this, "Map must have a non-empty title", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
            intent.putExtra(EXTRA_MAP_TITLE, title)
            getResult.launch(intent)
            dialog.dismiss()
        }

    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if(activityResult.resultCode == Activity.RESULT_OK){

                val userMap = activityResult.data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
                Log.i(TAG, "onActivityResult with new map title ${userMap.title}")
                userMaps.add(userMap)
                mapAdapter.notifyItemInserted(userMaps.size - 1)

            }
        }

    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First meetup", 37.444, -122.170)
                )
            ),
            UserMap("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            UserMap("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )
            ),
            UserMap("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                )
            )
        )
    }
}