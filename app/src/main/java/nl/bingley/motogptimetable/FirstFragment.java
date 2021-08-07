package nl.bingley.motogptimetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import nl.bingley.motogptimetable.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

	private FragmentFirstBinding binding;

	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState
	) {

		binding = FragmentFirstBinding.inflate(inflater, container, false);
		return binding.getRoot();

	}

	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TableRow row = new TableRow(binding.table.getContext());
		TextView text1 = new TextView(row.getContext());
		text1.setText("Hello");
		TextView text2 = new TextView(row.getContext());
		text2.setText("World");
		row.addView(text1);
		row.addView(text2);

		binding.table.addView(row);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

}