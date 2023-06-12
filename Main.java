import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    private static final double earthRad = 6371000; // in meters

    static class CargoInfo {
        private final int cargo;
        private final List<Double> coords;

        public CargoInfo(int cargo, List<Double> coords) {
            this.cargo = cargo;
            this.coords = coords;
        }

        public int getBox() {
            return cargo;
        }

        public List<Double> getCoords() {
            return coords;
        }
    }

    public static void main(String[] args) {
        List<Integer> initial = new ArrayList<>();
        List<CargoInfo> data = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("#.0");

        try (BufferedReader reader = new BufferedReader(new FileReader("src/supplementaryTest1.txt"))) {
            String line = reader.readLine();
            line = line.trim();
            String[] numbers = line.split("\\s+");
            int firstInt1 = Integer.parseInt(numbers[0].trim());
            int firstInt2 = Integer.parseInt(numbers[1].trim());

            initial.add(firstInt1);
            initial.add(firstInt2);

            while (line != null){
                line = reader.readLine();

                if (line == null) {break;}
                String[] dataset = line.split("\\s+");

                List<Double> doubles = new ArrayList<>();
                String cleanedString = dataset[1].substring(1, dataset[1].length() - 1);

                String[] coordinates = cleanedString.split(",");
                doubles.add(Double.parseDouble(coordinates[0]));
                doubles.add(Double.parseDouble(coordinates[1]));

                CargoInfo info = new CargoInfo(Integer.parseInt(dataset[0]), doubles);

                data.add(info);

                if (dataset.length > 2) {
                    List<Double> doubles1 = new ArrayList<>();
                    String cleanedString1 = dataset[3].substring(1, dataset[3].length() - 1);

                    String[] coordinates1 = cleanedString1.split(",");
                    doubles1.add(Double.parseDouble(coordinates1[0]));
                    doubles1.add(Double.parseDouble(coordinates1[1]));

                    CargoInfo info1 = new CargoInfo(Integer.parseInt(dataset[2]), doubles1);

                    data.add(info1);
                }

            }

        } catch (IOException e) {
            System.out.println("Error reading the input file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/test.txt"))) {

            int max = 0;
            int startIndex = 0;

            for (int i = 0; i < data.size(); i++){
                if (data.get(i).getBox() > max) {
                    max = data.get(i).getBox();
                    startIndex = i;
                }
            }

            int capacity = Math.min(initial.get(0), initial.get(1));
            capacity -= data.get(startIndex).getBox();
            List<Double> coord = data.get(startIndex).getCoords();
            List<Double> startCoord = coord;
            List<CargoInfo> closeLocations = new ArrayList<>();

            writer.write("Truck position: (" + startCoord.get(0) + "," + startCoord.get(1) + ")");
            writer.newLine();
            writer.write("Distance:0\tNumber of boxes:0\tPosition:(" + startCoord.get(0) + "," + startCoord.get(1) + ")");
            writer.newLine();

            double minDist;
            int currentIndex = startIndex;
            double currentDist = 0.0;
            data.remove(currentIndex);
            CargoInfo closestCargo = null;

            while (capacity > 0) {
                minDist = Double.MAX_VALUE;
                for (CargoInfo cargo : data){
                    double check = havDist(startCoord.get(0), startCoord.get(1), cargo.getCoords().get(0), cargo.getCoords().get(1));

                    if (minDist > check){
                        minDist = check;
                        closestCargo = cargo;
                        closeLocations.clear();
                        closeLocations.add(cargo);
                        currentDist = check;
                    } else if (minDist == check) {
                        closeLocations.add(cargo);
                    }
                }

                coord = closestCargo.getCoords();
                double nextLat = closeLocations.get(0).getCoords().get(0);

                if (closeLocations.size() > 1) {
                    for (CargoInfo closeLocation : closeLocations){
                        if (nextLat > closeLocation.getCoords().get(0)) {
                            nextLat = closeLocation.getCoords().get(0);
                            coord = closeLocation.getCoords();
                        }
                    }
                }

                int cargoBox = closestCargo.getBox();
                capacity -= cargoBox;
                data.remove(closestCargo);

                if (capacity < 0 && data.size() == 0) {
                    writer.close();
                } else if (capacity < 0) {
                    writer.write("Distance:" + format.format(currentDist) + "\tNumber of boxes:" + Math.abs(capacity) + "\tPosition:(" + coord.get(0) + "," + coord.get(1) + ")");
                } else {
                    writer.write("Distance:" + format.format(currentDist) + "\tNumber of boxes:0\tPosition:(" + coord.get(0) + "," + coord.get(1) + ")");
                    writer.newLine();
                }

            }

        } catch (IOException e) {
            System.out.println("Error writing to the output file: " + e.getMessage());
        }

    }

    private static double havDist(double lat1, double lon1, double lat2, double lon2) {
        double diffLat = Math.toRadians(lat2 - lat1);
        double diffLon = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(diffLat / 2), 2) + Math.pow(Math.sin(diffLon / 2), 2) *
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRad * c;
    }
}