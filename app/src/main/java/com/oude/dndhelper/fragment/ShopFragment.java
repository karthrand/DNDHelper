package com.oude.dndhelper.fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.oude.dndhelper.*;
import android.widget.*;
import android.view.View.*;
import android.content.*;
import com.oude.dndhelper.activity.*;

public class ShopFragment extends Fragment implements OnClickListener
{

	private Button item;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view=inflater.inflate(R.layout.shop,container,false);
        return view;
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	
	
	private void initView(){
		item = (Button)getActivity().findViewById(R.id.shop_item);
		item.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View p1)
	{
		
		switch(p1.getId()){
			case R.id.shop_item:
				Intent intent =new Intent(getActivity(), ItemActivity.class);
				startActivity(intent);
				break;
				default:
				break;
		}
	}
}
