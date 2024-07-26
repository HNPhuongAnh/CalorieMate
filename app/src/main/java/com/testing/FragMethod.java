package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragMethod extends Fragment {

    private RecyclerView recyclerViewMethods;
    private MethodAdapter methodAdapter;
    private List<DietMethod> dietMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_method, container, false);

        recyclerViewMethods = view.findViewById(R.id.recyclerViewMethods);
        recyclerViewMethods.setLayoutManager(new LinearLayoutManager(getContext()));

        dietMethods = new ArrayList<>();
        dietMethods.add(new DietMethod("Premium",
                "Kiểm soát calories",
                "https://www.morelandobgyn.com/hubfs/Imported_Blog_Media/GettyImages-854725402-1.jpg",
                "Đã được thời gian kiểm chứng và chứng minh. Không có thức ăn nào là quá giới hạn. "));
        dietMethods.add(new DietMethod("Low-Carb",
                "Tăng protein và giới hạn đạm để có kết quả nhanh hơn",
                "https://www.eatingwell.com/thmb/gect27EuqYBmDlTTPRhTf3_XXvE=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/easy-shrimp-scampi-with-zucchini-noodles-f68bce3c61ee402583cea2545d4a3145.jpeg",
                "Detailed info about Low-Carb diet..."));
        dietMethods.add(new DietMethod("Low-Fat",
                "Hạn chế chất béo và ăn những món ăn giàu chất dinh dưỡng để giảm cân.",
                "https://www.eatingwell.com/thmb/QYZnBgF72TIKI6-A--NyoPa6avY=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/greek-salmon-bowl-f681500cbe054bb1adb607ff55094075.jpeg",
                "Detailed info about Low-Fat diet..."));
        dietMethods.add(new DietMethod("High-Protein",
                "Lấy năng lượng từ protein để giảm bớt cơn đói và tạo cơ bắp khỏe.",
                "https://alokamedicare.in/wp-content/uploads/2022/07/High-Protein-Diet-Pros-Cons-Alokamedicare-1024x682.jpg",
                "Detailed info about High-Protein diet..."));
        dietMethods.add(new DietMethod("KETO",
                "Giới hạn tinh bột và tăng chất béo để đốt mỡ hiệu quả hơn",
                "https://www.diabetes.co.uk/wp-content/uploads/2019/01/What-to-eat-on-a-ketogenic-diet.png",
                "Detailed info about KETO diet..."));
        dietMethods.add(new DietMethod("Vegetarian",
                "Ăn một cách ngon miệng mà không cần thịt. Trở nên khỏe mạnh hơn với nhiều rau hơn.",
                "https://images.everydayhealth.com/images/diet-nutrition/what-is-a-vegan-diet-benefits-food-list-beginners-guide-alt-1440x810.jpg",
                "Detailed info about Vegetarian diet..."));


        methodAdapter = new MethodAdapter(getContext(), dietMethods, new MethodAdapter.OnMethodClickListener() {
            @Override
            public void onMethodClick(DietMethod dietMethod) {
                Intent intent = new Intent(getContext(), MethodDetailActivity.class);
                intent.putExtra("method_name", dietMethod.getName());
                intent.putExtra("method_details", dietMethod.getDetails());
                startActivity(intent);
            }
        });

        recyclerViewMethods.setAdapter(methodAdapter);

        return view;
    }
}
