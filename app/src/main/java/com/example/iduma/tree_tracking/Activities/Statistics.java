package com.example.iduma.tree_tracking.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.iduma.tree_tracking.Model.DeforestationModel;
import com.example.iduma.tree_tracking.Model.PlantingModel;
import com.example.iduma.tree_tracking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Statistics extends AppCompatActivity {

    private Button btnRetrieve;
    private TextView tvAfforestEconomics;
    private TextView tvAfforestNonEco;

    private TextView tvDeforestEconomics;
    private TextView tvDeforestNonEco;
    private DatabaseReference treeRef;
    private DatabaseReference defRef;
    private String noEcoAffoTree;
    private String noNEcoAffoTree;
    private String noEcoDefTree;
    private String noNonEcoDefTree;
    private String treeType;
    private String treeTypeDef;
    private int treenoEco;
    private int treenoNonEco;
    private int defTreeNoEco;
    private int defTreeNoNonEco;
    private Button displayAfforestation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvAfforestEconomics = findViewById(R.id.tvNoOfTrees);
        tvAfforestNonEco =findViewById(R.id.tvNoOfTreesN);
        displayAfforestation = findViewById(R.id.displayAfforestation);
        tvDeforestEconomics = findViewById(R.id.tvNoOfEcoTreeReported);
        tvDeforestNonEco = findViewById(R.id.tvNoOfNonEcoReported);


        treeRef= FirebaseDatabase.getInstance().getReference().child("Afforestation");
        defRef = FirebaseDatabase.getInstance().getReference().child("Deforestation");

        treeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                treenoEco=0;
                treenoNonEco=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PlantingModel model = ds.getValue(PlantingModel.class);
                    treeType = model.getTypeOfTrees();
                    Log.d("treeType",""+treeType);

                    if (treeType.matches("Economic")){
                        noEcoAffoTree =model.getNoOfTrees();
                        //Log.d("ecotree",""+noEcoAffoTree);
                        treenoEco=treenoEco+Integer.parseInt(noEcoAffoTree);
                    }else  if (treeType.matches("Non Economics")){
                        noNEcoAffoTree =model.getNoOfTrees();
                        treenoNonEco=treenoNonEco+Integer.parseInt(noNEcoAffoTree);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        defRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DeforestationModel defmodel = ds.getValue(DeforestationModel.class);
                    treeTypeDef = defmodel.getTypeOfTrees();
                    Log.d("treeType",""+treeType);

                    if (treeTypeDef.matches("Economic")){
                        noEcoDefTree =defmodel.getNoOfTrees();
                        Log.d("ecotree",""+noEcoDefTree);
                        defTreeNoEco=defTreeNoEco+Integer.parseInt(noEcoDefTree);
                    }else  if (treeTypeDef.matches("Non Economics")){
                        noNonEcoDefTree =defmodel.getNoOfTrees();
                        defTreeNoNonEco=defTreeNoNonEco+Integer.parseInt(noNonEcoDefTree);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        displayAfforestation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("treeNo",""+treenoEco);
                tvAfforestEconomics.setText(""+treenoEco);
                tvAfforestNonEco.setText(""+treenoNonEco);
                tvDeforestEconomics.setText(""+defTreeNoEco);
                tvDeforestNonEco.setText(""+defTreeNoNonEco);

            }
        });
    }

}
