package com.project.seg2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MetricSelection extends Activity implements OnClickListener {

	private Button btGasses, btTrees, btElectricity, btPopulation, btBuildOwn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection_metric);
		initialise();
	}

	private void initialise() {

		btGasses = (Button) findViewById(R.id.btGasses);
		btGasses.setOnClickListener(this);

		btTrees = (Button) findViewById(R.id.btTrees);
		btTrees.setOnClickListener(this);

		btElectricity = (Button) findViewById(R.id.btElectricity);
		btElectricity.setOnClickListener(this);

		btPopulation = (Button) findViewById(R.id.btPopulation);
		btPopulation.setOnClickListener(this);

		btBuildOwn = (Button) findViewById(R.id.btBuild);
		btBuildOwn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btGasses:
			startActivity(new Intent("com.project.seg2.WHICHGAS"));
			break;
		case R.id.btTrees:
			break;
		case R.id.btElectricity:
			break;
		case R.id.btPopulation:
			break;
		case R.id.btBuild:
			startActivity(new Intent("com.project.seg2.BUILDOWN"));
			break;

		}

	}
}
