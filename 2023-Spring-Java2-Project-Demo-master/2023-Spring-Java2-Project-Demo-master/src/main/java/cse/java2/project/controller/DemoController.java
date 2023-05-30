package cse.java2.project.controller;

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.annotation.PostConstruct;

@Controller
public class DemoController {

    public List<JSONObject> jsonList = new ArrayList<>();

    @PostConstruct
    public void readFiles(){
        try (BufferedReader br = new BufferedReader(new FileReader("java_questions.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                JSONTokener tokener = new JSONTokener(line);
                JSONObject jsonObject = new JSONObject(tokener);
                jsonList.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * This method is called when the user requests the root URL ("/") or "/demo".
     * In this demo, you can visit localhost:9090 or localhost:9090/demo to see the result.
     * @return the name of the view to be rendered
     * You can find the static HTML file in src/main/resources/templates/demo.html
     */
    @GetMapping({"/demo"})
    public String demo() {
        return "demo";
    }
    @GetMapping({"/home","/"})
    public String home() {
        return "Home";
    }
    @GetMapping({"/File"})
    public String File() {
        return "File";
    }
    @GetMapping({"/Part1"})
    public String Part1(Model model){
        int t = 0;
        int max = 0;
        int sum = 0;
        int c1 = 0, c2=0, c3=0, c4=0, c5 = 0;
        for(JSONObject object:jsonList) {
            int q = object.getInt("answer_count");
            if (object.getInt("answer_count") == 0) {
                t++;
            }
            if (q > max) {
                max = q;
            }
            sum += q;
            if (0 <= q && q < 20) {
                c1++;
            }
            if (20 <= q && q < 40) {
                c2++;

            }
            if (40 <= q && q < 60) {
                c3++;
            }
            if (60 <= q && q < 80) {
                c4++;
            }
            if (q > 80) {
                c5++;
            }
        }
        Float unansweredPct =  ((float)t/ jsonList.size()*100);
        System.out.println(t);
        int averageCount = sum/ jsonList.size();
        Map<String,Integer> map = new LinkedHashMap<>();
        map.put("0", t);
        map.put("1 to 20",c1);
        map.put("20 to 40",c2);
        map.put("40 to 60",c3);
        map.put("60 to 80",c4);
        map.put("Above 80",c5);
        model.addAttribute("unansweredPct",unansweredPct);
        model.addAttribute("averageCount",averageCount);
        model.addAttribute("max",max);
        model.addAttribute("dataMap",map);
        return "Answer";
    }
    @GetMapping({"/Part2"})
    public String Part2(Model model){
        int count = 0;
        int count2 = 0;
        int tenMin = 0;
        int oneH = 0;
        int fiveH = 0;
        int oneD = 0;
        int oneW = 0;
        int overOneW = 0;
        for(JSONObject object: jsonList){
            int creationDate = object.getInt("creation_date");
            if(object.getInt("answer_count")>0){
                JSONArray ansList = object.getJSONArray("answers");
                int flag = 0;
                int max2 = 0;
                int acAnsVote = 0;
                for(int i =0;i<ansList.length();i++){
                    JSONObject answer = ansList.getJSONObject(i);
                    if(answer.getInt("up_vote_count")>max2){
                        max2 = answer.getInt("up_vote_count");
                    }
                    if(answer.getBoolean("is_accepted")){
                        flag=1;
                        acAnsVote = answer.getInt("up_vote_count");
                        int ansCreationDate = answer.getInt("creation_date");
                        int duration = ansCreationDate - creationDate;
                        if(duration<600){
                            tenMin++;
                        }
                        else if(duration<3600){
                            oneH++;
                        }
                        else if(duration<18000){
                            fiveH++;
                        }
                        else if(duration<86400){
                            oneD++;
                        }
                        else if(duration<604800){
                            oneW++;
                        }
                        else{
                            overOneW++;
                        }
                        break;
                    }
                }
                if(flag==1){
                    count++;
                    if(acAnsVote<max2){
                        count2++;
                    }
                }
            }
        }
        Map<String,Integer> dataMap = new LinkedHashMap<>();
        dataMap.put("less than 10 minutes",tenMin);
        dataMap.put("10 minutes to 1 hour",oneH);
        dataMap.put("1 hour to 5 hours",fiveH);
        dataMap.put("5 hours to 1 day",oneD);
        dataMap.put("1 day to 1 week",oneW);
        dataMap.put("more than 1 week",overOneW);
        Float acceptedPct = ((float)count/ jsonList.size())*100;
        Float moreUpVotes = ((float)count2/count)*100;
        model.addAttribute("count",acceptedPct);
        model.addAttribute("count2",moreUpVotes);
        model.addAttribute("dataMap",dataMap);
        System.out.println(dataMap);
        return "Accepted_Answer";
    }
    @GetMapping({"/Part3"})
    public String Part3(Model model){
        Map<String,Integer> tagsMap = new HashMap<>();

        for(JSONObject object: jsonList){
            JSONArray tags = object.getJSONArray("tags");

            for(int i = 0;i<tags.length();i++) {
                String q = tags.getString(i);
                if (!q.equals("java")) {
                    if (tagsMap.containsKey(q)) {
                        int countTags = tagsMap.get(q);
                        tagsMap.put(q, countTags + 1);

                    } else {
                        tagsMap.put(q, 1);
                    }

                }
            }
        }
        model.addAttribute("dataMap",tagsMap);

        Map<String,Integer> upVoteMap = new HashMap<>();
        Map<String,Integer> viewMap = new HashMap<>();
        for(JSONObject object: jsonList){
            int upVoteCount =object.getInt("up_vote_count");
            int viewCount = object.getInt("view_count");
            JSONArray tags = object.getJSONArray("tags");
            for(int j =1;j<tags.length();j++){
                String key = "java, "+tags.getString(j);
                if(upVoteMap.containsKey(key)){
                    int countUpVotes = upVoteMap.get(key);
                    int countViews = viewMap.get(key);
                    upVoteMap.put(key, countUpVotes+upVoteCount);
                    viewMap.put(key,countViews+viewCount);
                }
                else{
                    upVoteMap.put(key,upVoteCount);
                    viewMap.put(key,viewCount);
                }

            }
        }
        Map<String,Integer> upVoteResMap = new LinkedHashMap<>();
        Map<String,Integer> viewResMap = new LinkedHashMap<>();
        upVoteMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(10)
                .forEachOrdered(e ->{upVoteResMap.put(e.getKey(),e.getValue());});
        viewMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(10)
                .forEachOrdered(e ->{viewResMap.put(e.getKey(),e.getValue());});
        model.addAttribute("dataMap2",upVoteResMap);
        model.addAttribute("dataMap3",viewResMap);
        return "Tags";
    }


    @GetMapping({"/Part4"})
    public String Part4(Model model){

        Map<Integer, Integer> dataMap = new HashMap<>();
        Map<Integer, Integer> answererMap = new HashMap<>();
        Map<Integer, Integer> commenterMap = new HashMap<>();
        Map<Integer, Integer> ansUserMap = new HashMap<>();
        Map<Integer, Integer> commUserMap = new HashMap<>();
        for (JSONObject object : jsonList) {
            List<Integer> list = new ArrayList<>();
            int count = 0;
            int answererCount = 0;
            int commenterCount = 0;
            int number = object.getInt("question_id");
            if (object.getInt("answer_count") > 0) {
                JSONArray answers = object.getJSONArray("answers");
                for (int i = 0; i < answers.length(); i++) {
                    JSONObject ans = answers.getJSONObject(i);

                    if (!ans.getJSONObject("owner").getString("user_type").equals("does_not_exist")) {
                        int answerer = ans.getJSONObject("owner").getInt("account_id");
                        if(ansUserMap.containsKey(answerer)){
                            int ca = ansUserMap.get(answerer);
                            ansUserMap.put(answerer,ca+1);
                        }
                        else{
                            ansUserMap.put(answerer,1);
                        }
                        int flag = 0;
                        for (int k = 0; k < list.size(); k++) {
                            if (list.get(k) == answerer) {
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            count++;
                            answererCount++;
                            list.add(answerer);
                        }
                    }
                    if (ans.getInt("comment_count") > 0) {
                        JSONArray comments = ans.getJSONArray("comments");
                        for (int j = 0; j < comments.length(); j++) {

                            JSONObject comm = comments.getJSONObject(j);
                            if (!comm.getJSONObject("owner").getString("user_type").equals("does_not_exist")) {
                                int commenter = comm.getJSONObject("owner").getInt("account_id");

                                if(commUserMap.containsKey(commenter)){
                                    int cc = commUserMap.get(commenter);
                                    commUserMap.put(commenter,cc+1);
                                }
                                else {
                                    commUserMap.put(commenter,1);
                                }
                                int flag2 = 0;
                                for (int t = 0; t < list.size(); t++) {
                                    if (list.get(t) == commenter) {
                                        flag2 = 1;
                                        break;
                                    }
                                }
                                if (flag2 == 0) {
                                    count++;
                                    commenterCount++;
                                    list.add(commenter);
                                }
                            }

                        }
                    }

                }
            }
            if(dataMap.containsKey(number)){
                System.out.println(number);
            }
            dataMap.put(number, count);
            answererMap.put(number,answererCount);
            commenterMap.put(number,commenterCount);
        }


        LinkedHashMap<Integer, Integer> topAnswererMap = ansUserMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        LinkedHashMap<Integer, Integer> topCommenterMap = commenterMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        model.addAttribute("dataMap",dataMap);
        model.addAttribute("dataMap1",answererMap);
        model.addAttribute("dataMap2",commenterMap);
        model.addAttribute("dataMap3",topAnswererMap);
        model.addAttribute("dataMap4",topCommenterMap);
        return "Users";
    }
}
