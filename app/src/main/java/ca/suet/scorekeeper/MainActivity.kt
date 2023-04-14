package ca.suet.scorekeeper

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.PreferenceManager
import ca.suet.scorekeeper.databinding.ActivityMainBinding

// A class for teams which has a score field and a function to manage score
class Team(val teamName: String){
    // starting score is 0
    public var score : Int = 0

    // score can be a positive integer only
    fun adjustPoint(point :Int){
        score+=point
        if(score<0){
            score = 0
        }
    }
}

class MainActivity :
    AppCompatActivity(), View.OnClickListener{
    lateinit var binding: ActivityMainBinding
    // create 2 teams
    public var team1 = Team("team1")
    public var team2 = Team("team2")
    // a team being scoring
    public var scoringteam = team1
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // using binding instead of findViewId
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // using a shared preferences to save and retrieve data in the form of key,value pair.
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // set up the listeners for both add and deduct button
        binding.deductPointButton.setOnClickListener(this)
        binding.addPointButton.setOnClickListener(this)

        // if edit text team names are changed, send a log
        binding.team1Name.setOnClickListener({_ ->
            Log.i("Click", "Edit team 1 name")
        })
        binding.team2Name.setOnClickListener({_ ->
            Log.i("Click", "Edit team 2 name")
        })

        // use the toggle button to toggle team being scoring
        binding.scoreTeamButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scoringteam = team1
            } else {
                scoringteam = team2
            }
        }
    }

    // when the activity is brought to the foreground again
    override fun onResume() {
        super.onResume()
        // retrieve the team names and scores and then update display
        binding.team1Name.setText(sharedPrefs.getString("team_1_name", "team1"))
        binding.team2Name.setText(sharedPrefs.getString("team_2_name", "team2"))
        team1.score = sharedPrefs.getString("team_1_score","0").toString().toInt()
        team2.score = sharedPrefs.getString("team_2_score","0").toString().toInt()
        binding.pointsGroup.check(sharedPrefs.getInt("spinner_point",0))
        updateDisplay()
    }

    // when the activity is about to be paused, such as another activity is being launched
    override fun onPause() {
        // create an editor to store data
        val editor = sharedPrefs.edit()
        // if the data need be stored
        if(sharedPrefs.getBoolean("switch_preference_1", false)) {
            // store the team names and scores in the editor
            editor.putString("team_1_name", binding.team1Name.text.toString())
            editor.putString("team_2_name", binding.team2Name.text.toString())
            editor.putString("team_1_score", binding.team1Score.text.toString())
            editor.putString("team_2_score", binding.team2Score.text.toString())
            // store the spinner point in the editor
            editor.putInt("spinner_point", binding.pointsGroup.checkedRadioButtonId)
            editor.putBoolean("scoring_team", binding.scoreTeamButton.isChecked)
            Log.i("onClick","scoring_team: "+binding.scoreTeamButton.isChecked)
        }
        // otherwise, the data is not going to be stored
        else{
            // cleared the editor and restore the perference setting
            editor.clear()
            editor.putBoolean("switch_preference_1", false)
        }

        // apply the editor
        editor.apply()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("ItemID", item.itemId.toString())
        when(item.itemId){
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.menu_about -> {
                Toast.makeText(this, "IOT1009 Mobile Application Development", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // to manage buttons
    override fun onClick(v: View?) {
        Log.i("onClick", "A button was pressed")
        // the scoring point is changed when the radio buttons is updated
        var scoringPoint = when (binding.pointsGroup.checkedRadioButtonId) {
            R.id.one_point_button ->  1
            R.id.two_points_button -> 2
            else -> 3
        }

        // the score is updated when the add and deduct buttons are pressed
        when (v?.id) {
            R.id.add_point_button -> scoringteam.adjustPoint(scoringPoint)
            R.id.deduct_point_button -> scoringteam.adjustPoint(-scoringPoint)

            else -> Log.e("onClick", "Something went wrong")
        }

        // update display (scores) when the any button is pressed
        updateDisplay()
    }

    // to show updated score in the display
    fun updateDisplay(){

        binding.team1Score.setText(team1.score.toString())
        binding.team2Score.setText(team2.score.toString())



    }

}