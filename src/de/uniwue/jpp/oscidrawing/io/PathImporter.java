package de.uniwue.jpp.oscidrawing.io;

import de.uniwue.jpp.oscidrawing.generation.pathutils.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PathImporter {
//    Diese Methode erhält eine Liste von Strings, von denen jeder eine Zeile einer Textdatei darstellen soll.
//    Falls alle Zeilen dem eben beschriebenen Format entsprechen,
//    soll eine Liste der durch die Zeilen beschriebenen Punkte, verpackt in ein Optional, zurückgegeben werden.
//    Falls eine der Zeilen nicht dem beschriebenen Format entspricht und deswegen nicht in einen Point übersetzt werden kann,
//    soll stattdessen ein leeres Optional zurückgegeben werden.

    public static Optional<List<Point>> fromString(List<String> lines) {
        if (lines == null) {
            return Optional.empty();
        }
        if (lines.isEmpty()) {
            return Optional.of(new ArrayList<>());
        }

        List<Point> PointsList = new ArrayList<>();
        for (String line : lines) {
            String[] Koordinaten = line.split(",");
            if (Koordinaten.length != 2) {
                return Optional.empty();   //ungültig
            }


            try {
                double x = Double.parseDouble(Koordinaten[0]);
                double y = Double.parseDouble(Koordinaten[1]);

                Point point = new Point(x, y);
                PointsList.add(point);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }

        }
        return Optional.of(PointsList);
    }

    public static Optional<List<Point>> fromFile(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            return fromString(lines);

        } catch (IOException e) {

            return Optional.empty();
        }

    }
}
