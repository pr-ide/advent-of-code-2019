package ru.ural;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {
    public static void main(String... arg) {
        List<Day6.Node> nodes = new ArrayList<>();

        Path path = Paths.get("/home/kirill/Development/GIT/advent-of-code-2019/Java/static/day10");
        List<Asteroid> asteroids = new ArrayList<>();
        try (Stream<String> lines = Files.lines(path)) {
            int iLine = 0;
            for (String line : lines.collect(Collectors.toList())) {
                int i = 0;
                for (String s : line.split("")) {
                    if ("#".equals(s)) asteroids.add(new Asteroid(new Point(i, iLine)));
                    i++;
                }
                iLine++;
            }
        } catch (
                IOException ex) {
            // do something or re-throw...
        }

        asteroids.forEach(asteroid -> asteroid.calculateAssInView(asteroids));
        int astCount = asteroids.size();
        Asteroid res = asteroids.stream().max(Comparator.comparingInt(Asteroid::getView)).orElseThrow(RuntimeException::new);
        System.out.println(res.toString());
        int remainToDestroy = 200;
        Asteroid a200 = null;
        while (a200 == null && (asteroids.size() > astCount - 200 && asteroids.size() > 1)) {
            List<Asteroid> TODESTROY = res.radarCalculateAssInView();

            if (TODESTROY.size() > remainToDestroy || asteroids.size() - 1 - TODESTROY.size() == 0) {
                int index = TODESTROY.size() - remainToDestroy;
                a200 = TODESTROY.get(remainToDestroy);
            }

            remainToDestroy -= TODESTROY.size();
            asteroids.removeAll(TODESTROY);

        }

        assert a200 != null;
        System.out.println(a200.toString());
    }
}

class Asteroid {
    private Point point;
    private int view = 0;
    private List<Asteroid> viewList = new ArrayList<>();

    public Point getPoint() {
        return point;
    }

    public int getView() {
        return view;
    }

    public Asteroid(Point point) {
        this.point = point;
    }

    public void calculateAssInView(List<Asteroid> asteroids) {

        final List<Asteroid> otherAsteroids = asteroids.stream().filter(asteroid -> !asteroid.getPoint().equals(getPoint())).collect(Collectors.toList());

        otherAsteroids.forEach(asteroid -> {
            boolean isInView = otherAsteroids.stream()
                    .filter(asteroid1 -> !asteroid1.getPoint().equals(asteroid.getPoint()))
                    .map(asteroid1 -> asteroid1.isAsteroidBetween(this, asteroid))
                    .allMatch(Boolean.FALSE::equals);
            if (isInView) {
                viewList.add(asteroid);
                view++;
            }
            ;
        });

    }

    public List<Asteroid> radarCalculateAssInView() {

        List<Asteroid> res = new ArrayList<>();

        for (int i = 0; i < 36000; i+=1) {
            Point position = getPosition(this.getPoint(),
                    viewList.stream().
                            map(asteroid -> distance(this.point, asteroid.getPoint())).
                            max(Integer::compareTo).orElse(0),
//                    400,
                    i);
            Asteroid lineDetectAsteroid = isLineDetectAsteroid(this, viewList, position);
            if(lineDetectAsteroid != null && res.stream().noneMatch(asteroid -> asteroid.getPoint().equals(lineDetectAsteroid.getPoint()))) {
                res.add(lineDetectAsteroid);
            }
        }

        return res;

    }

    private Point getPosition(Point center, int radius, double angle) {
        angle /= 100;

        angle = angle - 90;
        Point p = new Point(Math.toIntExact(Math.round(center.x + radius * Math.cos(Math.toRadians(angle)))),
                Math.toIntExact(Math.round(center.y + radius * Math.sin(Math.toRadians(angle)))));

        return p;
    }


    private boolean isAsteroidBetween(Asteroid start, Asteroid end) {

        List<Asteroid> asteroids = new ArrayList<>();
        asteroids.add(this);

        Asteroid lineDetectAsteroid = isLineDetectAsteroid(start, asteroids, end.getPoint());
        return lineDetectAsteroid != null;

    }

    private static Asteroid isLineNotDetectAsteroid(Asteroid start, List<Asteroid> visibleAsteroids, Point point) {

        return visibleAsteroids.stream().map(asteroid -> {
            boolean b = (
                    ((start.getPoint().getX() * (asteroid.getPoint().getY() - point.getY()))
                            + (asteroid.getPoint().getX() * (point.getY() - start.getPoint().getY()))
                            + (point.getX() * (start.getPoint().getY() - asteroid.getPoint().getY()))
                    ) / 2) == 0;

            if (b) {


                boolean containsX = Math.min(start.getPoint().getX(), point.getX()) <= asteroid.getPoint().getX()
                        && Math.max(start.getPoint().getX(), point.getX()) >= asteroid.getPoint().getX();
                boolean containsY = Math.min(start.getPoint().getY(), point.getY()) <= asteroid.getPoint().getY()
                        && Math.max(start.getPoint().getY(), point.getY()) >= asteroid.getPoint().getY();

                if (containsX && containsY) {
                    return null;
                } else {
                    return asteroid;
                }

            }
            return asteroid;
        }).filter(Objects::nonNull).findAny().orElse(null);

    }

    private static Integer distance(Point point1, Point point2) {
        return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
    }


    private static Asteroid isLineDetectAsteroid(Asteroid start, List<Asteroid> visibleAsteroids, Point point) {

        return visibleAsteroids.stream().map(asteroid -> {
            boolean b = (
                    ((start.getPoint().getX() * (asteroid.getPoint().getY() - point.getY()))
                            + (asteroid.getPoint().getX() * (point.getY() - start.getPoint().getY()))
                            + (point.getX() * (start.getPoint().getY() - asteroid.getPoint().getY()))
                    ) / 2) == 0;

            if (b) {


                boolean containsX = Math.min(start.getPoint().getX(), point.getX()) <= asteroid.getPoint().getX()
                        && Math.max(start.getPoint().getX(), point.getX()) >= asteroid.getPoint().getX();
                boolean containsY = Math.min(start.getPoint().getY(), point.getY()) <= asteroid.getPoint().getY()
                        && Math.max(start.getPoint().getY(), point.getY()) >= asteroid.getPoint().getY();

                if (containsX && containsY) {
                    return asteroid;
                } else {
                    return null;
                }

            }
            return null;
        }).filter(Objects::nonNull).findAny().orElse(null);

    }

    private boolean isNumberBetween(Double num, Double start, Double end) {
        return Math.abs(start - end) > Math.abs(num - end);
    }

    @Override
    public String toString() {
        return "Asteroid{" +
                "point=" + point +
                ", view=" + view +
                '}';
    }
}