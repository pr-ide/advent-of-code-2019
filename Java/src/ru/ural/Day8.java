package ru.ural;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Day8 {


    public static void main(String[] args) {
        // write your code here

        List<Layer> res = new ArrayList<>();
        int width = 25;
        int height = 6;

        File file = new File("/home/kirill/Documents/8day");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                List<Integer> collect = Arrays.stream(line.split("")).map(Integer::parseInt).collect(Collectors.toList());

                Iterator<Integer> iterator = collect.iterator();
                int iw = 0;
                int ih = 0;
                Layer iLayer = new Layer(1);
                while (iterator.hasNext()) {


                    if (ih == iLayer.getListList().size()) {
                        iLayer.getListList().add(new ArrayList<>());
                    }
                    List<Integer> integers = iLayer.getListList().get(ih);
                    integers.add(iterator.next());

                    iw++;

                    if (iw == width) {
                        iw = 0;
                        ih++;
                    }

                    if (ih == height) {
                        ih = 0;
                        res.add(iLayer);
                        iLayer = new Layer(res.size() + 1);
                    }


                }

                // process the line.
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Layer fRes = new Layer(res, width, height);
        fRes.getListList().forEach(integers -> System.out.println(integers.stream().map(integer -> {
            switch (integer) {
                case 0:
                    return " ";
                case 1:
                    return "#";
            }
            return " ";
        }).collect(Collectors.joining(""))));

        res.sort((o1, o2) -> o2.getNumCount(0).compareTo(o1.getNumCount(0)));
        Layer i = res.get(res.size() - 1);
        System.out.println(i.getNumCount(1) * i.getNumCount(2));


        return;

    }

    static class Layer {
        int i = 0;
        List<List<Integer>> listList = new ArrayList<>();

        public List<List<Integer>> getListList() {
            return listList;
        }

        public void setListList(List<List<Integer>> listList) {
            this.listList = listList;
        }

        public Layer(int i) {
            this.i = i;
        }

        private Integer getXY(int x, int y) {
            return getListList().get(y).get(x);
        }

        public Layer(List<Layer> layers, int w, int h) {
            this.i = 0;

            for (int m = 0; m < h; m++) {
                listList.add(new ArrayList<>());
                for (int n = 0; n < w; n++) {
                    for (Layer layer : layers) {
                        Integer xy = layer.getXY(n, m);
                        if (xy != 2) {
                            listList.get(m).add(xy);
                            break;

                        }
                    }
                }
            }
        }

        public int getI() {
            return i;
        }

        public Integer getNumCount(int i) {
            return Math.toIntExact(getListList().stream().flatMap(Collection::stream).filter(integer -> integer.equals(i)).count());

        }
    }
}
