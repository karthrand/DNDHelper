package com.oude.dndhelper.fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.oude.dndhelper.*;

public class ShopFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view=inflater.inflate(R.layout.shop,container,false);
        TextView content= (TextView) view.findViewById(R.id.content);
        content.setText("商店");
        return view;
	}

}
