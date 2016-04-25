package android.captainhampton.com.spaceapps;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class FlightActivity extends AppCompatActivity {

    SparseArray<Group> groups = new SparseArray<Group>();
    List<String> callsignArr, headingArr, lonArr, latArr, altitudeArr;
    //private ImageButton ibCloseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*ibCloseBtn = (ImageButton) findViewById(R.id.ibCloseBtn);
        ibCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavUtils.navigateUpFromSameTask(this);
            }
        });*/

        callsignArr = UserLocationActivity.callsignArr;
        headingArr = UserLocationActivity.headingArr;
        lonArr = UserLocationActivity.lonArr;
        latArr = UserLocationActivity.latArr;
        altitudeArr = UserLocationActivity.altitudeArr;

        insertData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }

    public void insertData() {
        for (int j = 0; j < 5; j++) {
            Group group = new Group("Call Sign: " + callsignArr.get(j));
            group.children.add("Heading: " + headingArr.get(j));
            group.children.add("Longtitude: " + lonArr.get(j));
            group.children.add("Latitude: " + latArr.get(j));
            group.children.add("Altitude: " + altitudeArr.get(j));
            groups.append(j, group);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flight, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings || id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
