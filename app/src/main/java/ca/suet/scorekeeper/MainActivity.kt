package ca.suet.scorekeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.util.Log
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

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    // create 2 teams
    public var team1 = Team("team1")
    public var team2 = Team("team2")
    // a team being scoring
    public var scoringteam = team1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // using binding instead of findViewId
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.team1Score.text = team1.score.toString()
        binding.team2Score.text = team2.score.toString()
    }
}