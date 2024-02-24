package com.robypomper.smartvan.smart_van.android.commons;


/** @noinspection unused*/
public class SVSpecs extends SVSpecGroup {

    // Constants

    public final static String ROOT_NAME = "SmartVan Box";
    public final static String SEPARATOR = ">";
    public final static String SEPARATOR_FORMATTED = " > ";


    // Static utils methods

    public static SVSpec fromPath(String specPath) {
        if (specPath.compareTo(ROOT_NAME) == 0)
            return SVBox;
        String[] path = specPath.split(SEPARATOR);
        SVSpecGroup group = SVBox;
        SVSpec spec = null;
        for (String name : path) {
            spec = null;
            for (SVSpec s: group.getSpecs())
                if (s.getName().equals(name.trim())) {
                    spec = s;
                    break;
                }
            if (spec == null)
                return null;
            if (spec instanceof SVSpecGroup)
                group = (SVSpecGroup) spec;
            else
                return spec;
        }
        return spec;
    }


    // Constructors

    public SVSpecs() {
        super(ROOT_NAME, null);
    }


    // SVBox and main SpecsGroups instances

    public static final SVSpecs SVBox = new SVSpecs();

    public final PositionClass Position = new PositionClass(this);
    public final SensorsClass Sensors = new SensorsClass(this);
    public final ServicesClass Services = new ServicesClass(this);
    public final EnergyClass Energy = new EnergyClass(this);
    public final IoTBoxClass IoT = new IoTBoxClass(this);


    // Classes for Main SpecsGroups

    public static class PositionClass extends SVSpecGroup {
        public PositionClass(SVSpecGroup parent) { super("Position", parent); }
        public final GNSSClass GNSS = new GNSSClass(this);
        public final InertialClass Inertial = new InertialClass(this);
    }
    public static class SensorsClass extends SVSpecGroup {
        public SensorsClass(SVSpecGroup parent) { super("Sensors", parent); }
        public final LivingClass Living = new LivingClass(this);
        public final EngineClass Engine = new EngineClass(this);
    }
    public static class ServicesClass extends SVSpecGroup {
        public ServicesClass(SVSpecGroup parent) { super("Services", parent); }
        public final ControllersClass Controllers = new ControllersClass(this);
        public final ActuatorsClass Actuators = new ActuatorsClass(this);
    }
    public static class EnergyClass extends SVSpecGroup {
        public EnergyClass(SVSpecGroup parent) { super("Energy", parent); }
        public final StorageClass Storage = new StorageClass(this);
        public final GenerationClass Generation = new GenerationClass(this);
        public final ConsumptionClass Consumption = new ConsumptionClass(this);
    }
    public static class IoTBoxClass extends SVSpecGroup {
        public IoTBoxClass(SVSpecGroup parent) { super("IoT Box", parent); }
        public final IoTEnergyClass Battery = new IoTEnergyClass(this);
        public final InternetClass Internet = new InternetClass(this);
        public final AccessPointClass AccessPoint = new AccessPointClass(this);
        public final MediaClass Media = new MediaClass(this);
        public final MonitorClass Monitor = new MonitorClass(this);
    }


    // Classes for Position sub SpecsGroup

    public static class GNSSClass extends SVSpecGroup {
        public GNSSClass(SVSpecGroup parent) { super("GNSS", parent); }
        public final SVSpec Latitude = new SVSpec("Latitude", this);
        public final SVSpec Latitude_Degrees = new SVSpec("Latitude Degrees", this);
        public final SVSpec Longitude = new SVSpec("Longitude", this);
        public final SVSpec Longitude_Degrees = new SVSpec("Longitude Degrees", this);
        public final SVSpec Altitude = new SVSpec("Altitude", this);
        public final SVSpec Speed = new SVSpec("Speed", this);
        public final SVSpec Course = new SVSpec("Course", this);
        public final SVSpec Available_Satellites = new SVSpec("Available Satellites", this);
        public final SVSpec Power_Module = new SVSpec("Power Module", this);
    }
    public static class InertialClass extends SVSpecGroup {
        public InertialClass(SVSpecGroup parent) {
            super("Inertial", parent);
        }
        public final SVSpec Roll = new SVSpec("Roll", this);
        public final SVSpec Pitch = new SVSpec("Pitch", this);
        public final SVSpec Yaw = new SVSpec("Yaw", this);
        public final SVSpec Acceleration_X = new SVSpec("Acceleration X", this);
        public final SVSpec Acceleration_y = new SVSpec("Acceleration Y", this);
        public final SVSpec Acceleration_Z = new SVSpec("Acceleration Z", this);
        public final SVSpec Gyroscope_X = new SVSpec("Gyroscope X", this);
        public final SVSpec Gyroscope_y = new SVSpec("Gyroscope Y", this);
        public final SVSpec Gyroscope_Z = new SVSpec("Gyroscope Z", this);
    }


    // Classes for Sensors sub SpecsGroup

    public static class LivingClass extends SVSpecGroup {
        public LivingClass(SVSpecGroup parent) { super("Living", parent); }
        public final SVSpec Pressure = new SVSpec("Pressure", this);
        public final SVSpec Temperature = new SVSpec("Temperature", this);
        public final SVSpec Humidity = new SVSpec("Humidity", this);
        public final SVSpec Lux = new SVSpec("Lux", this);
        public final SVSpec Analog_1_N = new SVSpec("Analog 1-N", this);
    }
    public static class EngineClass extends SVSpecGroup {
        public EngineClass(SVSpecGroup parent) {
            super("Engine", parent);
        }
        // public final Spec XXX = new Spec("XXX", this);
    }


    // Classes for Services sub SpecsGroup

    public static class ControllersClass extends SVSpecGroup {
        public ControllersClass(SVSpecGroup parent) { super("Controllers", parent); }
        public final ControllersBinaryClass Binary = new ControllersBinaryClass(this);
        public final ControllersPercentageClass Percentage = new ControllersPercentageClass(this);
    }
    public static class ControllersBinaryClass extends SVSpecGroup {
        public ControllersBinaryClass(SVSpecGroup parent) { super("Binary", parent); }
        public final SVSpec Binary_1 = new SVSpec("Binary 1", this);
        public final SVSpec Binary_2 = new SVSpec("Binary 2", this);
        public final SVSpec Binary_3 = new SVSpec("Binary 3", this);
        public final SVSpec Binary_4 = new SVSpec("Binary 4", this);
        public final SVSpec Binary_5 = new SVSpec("Binary 5", this);
        public final SVSpec Binary_6 = new SVSpec("Binary 6", this);
        public final SVSpec Binary_7 = new SVSpec("Binary 7", this);
        public final SVSpec Binary_8 = new SVSpec("Binary 8", this);
        public final SVSpec Binary_9 = new SVSpec("Binary 9", this);
        public final SVSpec Binary_10 = new SVSpec("Binary 10", this);
        public final SVSpec Binary_11 = new SVSpec("Binary 11", this);
        public final SVSpec Binary_12 = new SVSpec("Binary 12", this);
        public final SVSpec Binary_13 = new SVSpec("Binary 13", this);
        public final SVSpec Binary_14 = new SVSpec("Binary 14", this);
        public final SVSpec Binary_15 = new SVSpec("Binary 15", this);
        public final SVSpec Binary_16 = new SVSpec("Binary 16", this);
    }
    public static class ControllersPercentageClass extends SVSpecGroup {
        public ControllersPercentageClass(SVSpecGroup parent) { super("Percentage", parent); }
        public final SVSpec Percentage_1 = new SVSpec("Percentage 1", this);
        public final SVSpec Percentage_2 = new SVSpec("Percentage 2", this);
        public final SVSpec Percentage_3 = new SVSpec("Percentage 3", this);
        public final SVSpec Percentage_4 = new SVSpec("Percentage 4", this);
        public final SVSpec Percentage_5 = new SVSpec("Percentage 5", this);
        public final SVSpec Percentage_6 = new SVSpec("Percentage 6", this);
        public final SVSpec Percentage_7 = new SVSpec("Percentage 7", this);
        public final SVSpec Percentage_8 = new SVSpec("Percentage 8", this);
        public final SVSpec Percentage_9 = new SVSpec("Percentage 9", this);
        public final SVSpec Percentage_10 = new SVSpec("Percentage 10", this);
        public final SVSpec Percentage_11 = new SVSpec("Percentage 11", this);
        public final SVSpec Percentage_12 = new SVSpec("Percentage 12", this);
        public final SVSpec Percentage_13 = new SVSpec("Percentage 13", this);
        public final SVSpec Percentage_14 = new SVSpec("Percentage 14", this);
        public final SVSpec Percentage_15 = new SVSpec("Percentage 15", this);
        public final SVSpec Percentage_16 = new SVSpec("Percentage 16", this);
    }
    public static class ActuatorsClass extends SVSpecGroup {
        public ActuatorsClass(SVSpecGroup parent) {
            super("Actuators", parent);
        }
        public final ActuatorsSwitchLowClass SwitchLow = new ActuatorsSwitchLowClass(this);
        public final SVSpec Switch_Low_Avg_Current = new SVSpec("Switch Low Avg Current", this);
        public final SVSpec Switch_Low_Max_Current = new SVSpec("Switch Low Max Current", this);
        public final ActuatorsSwitchHighClass SwitchHigh = new ActuatorsSwitchHighClass(this);
        public final SVSpec Switch_High_Avg_Current = new SVSpec("Switch High Avg Current", this);
        public final ActuatorsDimmerLowClass DimmerLow = new ActuatorsDimmerLowClass(this);
        public final SVSpec Dimmer_Low_Avg_Current = new SVSpec("Dimmer Low Avg Current", this);
        public final SVSpec Dimmer_Low_Max_Current = new SVSpec("Dimmer Low Max Current", this);
        public final ActuatorsDimmerHighClass DimmerHigh = new ActuatorsDimmerHighClass(this);
        public final SVSpec Dimmer_High_Avg_Current = new SVSpec("Dimmer High Avg Current", this);
    }
    public static class ActuatorsSwitchLowClass extends SVSpecGroup {
        public ActuatorsSwitchLowClass(SVSpecGroup parent) { super("SwitchLow", parent); }
        public final SVSpec SwitchLow_1 = new SVSpec("SwitchLow 1", this);
        public final SVSpec SwitchLow_2 = new SVSpec("SwitchLow 2", this);
        public final SVSpec SwitchLow_3 = new SVSpec("SwitchLow 3", this);
        public final SVSpec SwitchLow_4 = new SVSpec("SwitchLow 4", this);
        public final SVSpec SwitchLow_5 = new SVSpec("SwitchLow 5", this);
        public final SVSpec SwitchLow_6 = new SVSpec("SwitchLow 6", this);
        public final SVSpec SwitchLow_7 = new SVSpec("SwitchLow 7", this);
        public final SVSpec SwitchLow_8 = new SVSpec("SwitchLow 8", this);
        public final SVSpec SwitchLow_9 = new SVSpec("SwitchLow 9", this);
        public final SVSpec SwitchLow_10 = new SVSpec("SwitchLow 10", this);
        public final SVSpec SwitchLow_11 = new SVSpec("SwitchLow 11", this);
        public final SVSpec SwitchLow_12 = new SVSpec("SwitchLow 12", this);
        public final SVSpec SwitchLow_13 = new SVSpec("SwitchLow 13", this);
        public final SVSpec SwitchLow_14 = new SVSpec("SwitchLow 14", this);
        public final SVSpec SwitchLow_15 = new SVSpec("SwitchLow 15", this);
        public final SVSpec SwitchLow_16 = new SVSpec("SwitchLow 16", this);
    }
    public static class ActuatorsSwitchHighClass extends SVSpecGroup {
        public ActuatorsSwitchHighClass(SVSpecGroup parent) { super("SwitchHigh", parent); }
        public final SVSpec SwitchHigh_1 = new SVSpec("SwitchHigh 1", this);
        public final SVSpec SwitchHigh_2 = new SVSpec("SwitchHigh 2", this);
        public final SVSpec SwitchHigh_3 = new SVSpec("SwitchHigh 3", this);
        public final SVSpec SwitchHigh_4 = new SVSpec("SwitchHigh 4", this);
        public final SVSpec SwitchHigh_5 = new SVSpec("SwitchHigh 5", this);
        public final SVSpec SwitchHigh_6 = new SVSpec("SwitchHigh 6", this);
        public final SVSpec SwitchHigh_7 = new SVSpec("SwitchHigh 7", this);
        public final SVSpec SwitchHigh_8 = new SVSpec("SwitchHigh 8", this);
        public final SVSpec SwitchHigh_9 = new SVSpec("SwitchHigh 9", this);
        public final SVSpec SwitchHigh_10 = new SVSpec("SwitchHigh 10", this);
        public final SVSpec SwitchHigh_11 = new SVSpec("SwitchHigh 11", this);
        public final SVSpec SwitchHigh_12 = new SVSpec("SwitchHigh 12", this);
        public final SVSpec SwitchHigh_13 = new SVSpec("SwitchHigh 13", this);
        public final SVSpec SwitchHigh_14 = new SVSpec("SwitchHigh 14", this);
        public final SVSpec SwitchHigh_15 = new SVSpec("SwitchHigh 15", this);
        public final SVSpec SwitchHigh_16 = new SVSpec("SwitchHigh 16", this);
    }
    public static class ActuatorsDimmerLowClass extends SVSpecGroup {
        public ActuatorsDimmerLowClass(SVSpecGroup parent) { super("DimmerLow", parent); }
        public final SVSpec DimmerLow_1 = new SVSpec("DimmerLow 1", this);
        public final SVSpec DimmerLow_2 = new SVSpec("DimmerLow 2", this);
        public final SVSpec DimmerLow_3 = new SVSpec("DimmerLow 3", this);
        public final SVSpec DimmerLow_4 = new SVSpec("DimmerLow 4", this);
        public final SVSpec DimmerLow_5 = new SVSpec("DimmerLow 5", this);
        public final SVSpec DimmerLow_6 = new SVSpec("DimmerLow 6", this);
        public final SVSpec DimmerLow_7 = new SVSpec("DimmerLow 7", this);
        public final SVSpec DimmerLow_8 = new SVSpec("DimmerLow 8", this);
        public final SVSpec DimmerLow_9 = new SVSpec("DimmerLow 9", this);
        public final SVSpec DimmerLow_10 = new SVSpec("DimmerLow 10", this);
        public final SVSpec DimmerLow_11 = new SVSpec("DimmerLow 11", this);
        public final SVSpec DimmerLow_12 = new SVSpec("DimmerLow 12", this);
        public final SVSpec DimmerLow_13 = new SVSpec("DimmerLow 13", this);
        public final SVSpec DimmerLow_14 = new SVSpec("DimmerLow 14", this);
        public final SVSpec DimmerLow_15 = new SVSpec("DimmerLow 15", this);
        public final SVSpec DimmerLow_16 = new SVSpec("DimmerLow 16", this);
    }
    public static class ActuatorsDimmerHighClass extends SVSpecGroup {
        public ActuatorsDimmerHighClass(SVSpecGroup parent) { super("DimmerHigh", parent); }
        public final SVSpec DimmerHigh_1 = new SVSpec("DimmerHigh 1", this);
        public final SVSpec DimmerHigh_2 = new SVSpec("DimmerHigh 2", this);
        public final SVSpec DimmerHigh_3 = new SVSpec("DimmerHigh 3", this);
        public final SVSpec DimmerHigh_4 = new SVSpec("DimmerHigh 4", this);
        public final SVSpec DimmerHigh_5 = new SVSpec("DimmerHigh 5", this);
        public final SVSpec DimmerHigh_6 = new SVSpec("DimmerHigh 6", this);
        public final SVSpec DimmerHigh_7 = new SVSpec("DimmerHigh 7", this);
        public final SVSpec DimmerHigh_8 = new SVSpec("DimmerHigh 8", this);
        public final SVSpec DimmerHigh_9 = new SVSpec("DimmerHigh 9", this);
        public final SVSpec DimmerHigh_10 = new SVSpec("DimmerHigh 10", this);
        public final SVSpec DimmerHigh_11 = new SVSpec("DimmerHigh 11", this);
        public final SVSpec DimmerHigh_12 = new SVSpec("DimmerHigh 12", this);
        public final SVSpec DimmerHigh_13 = new SVSpec("DimmerHigh 13", this);
        public final SVSpec DimmerHigh_14 = new SVSpec("DimmerHigh 14", this);
        public final SVSpec DimmerHigh_15 = new SVSpec("DimmerHigh 15", this);
        public final SVSpec DimmerHigh_16 = new SVSpec("DimmerHigh 16", this);
    }


    // Classes for Energy sub SpecsGroup

    public static class StorageClass extends SVSpecGroup {
        public StorageClass(SVSpecGroup parent) { super("Storage", parent); }
        public final SVSpec Voltage = new SVSpec("Voltage", this);
        public final SVSpec Percentage = new SVSpec("Percentage", this);
        public final SVSpec Min_Voltage = new SVSpec("Min Voltage", this);
        public final SVSpec Max_Voltage = new SVSpec("Max Voltage", this);
    }
    public static class GenerationClass extends SVSpecGroup {
        public GenerationClass(SVSpecGroup parent) { super("Generation", parent); }
        public final SVSpec Current = new SVSpec("Current", this);
        public final SVSpec Voltage = new SVSpec("Voltage", this);
        public final SVSpec Power = new SVSpec("Power", this);
        public final SVSpec Percentage = new SVSpec("Percentage", this);
        public final SVSpec Max_Power = new SVSpec("Max Power", this);
    }
    public static class ConsumptionClass extends SVSpecGroup {
        public ConsumptionClass(SVSpecGroup parent) {
            super("Consumption", parent);
        }
        public final SVSpec Current = new SVSpec("Current", this);
        public final SVSpec Voltage = new SVSpec("Voltage", this);
        public final SVSpec Power = new SVSpec("Power", this);
        public final SVSpec Percentage = new SVSpec("Percentage", this);
        public final SVSpec Max_Power = new SVSpec("Max Power", this);
    }


    // Classes for IoT sub SpecsGroup

    public static class IoTEnergyClass extends SVSpecGroup {
        public IoTEnergyClass(SVSpecGroup parent) { super("Energy", parent); }
        public final SVSpec Status = new SVSpec("Status", this);
        public final SVSpec Percentage = new SVSpec("Percentage", this);
        public final SVSpec Voltage = new SVSpec("Voltage", this);
    }
    public static class InternetClass extends SVSpecGroup {
        public InternetClass(SVSpecGroup parent) { super("Internet", parent); }
        public final SVSpec Status = new SVSpec("Status", this);
        public final SVSpec SIM_Status = new SVSpec("SIM Status", this);
        public final SVSpec Percentage = new SVSpec("Percentage", this);
        public final SVSpec Max_Download = new SVSpec("Max Download", this);
        public final SVSpec Download = new SVSpec("Download", this);
        public final SVSpec Downloaded = new SVSpec("Downloaded", this);
        public final SVSpec Upload = new SVSpec("Upload", this);
        public final SVSpec Uploaded = new SVSpec("Uploaded", this);
        public final SVSpec Power_Module = new SVSpec("Power Module", this);
    }
    public static class AccessPointClass extends SVSpecGroup {
        public AccessPointClass(SVSpecGroup parent) { super("AccessPoint", parent); }
        // public final Spec XXX = new Spec("XXX", this);
    }
    public static class MediaClass extends SVSpecGroup {
        public MediaClass(SVSpecGroup parent) { super("Media", parent); }
        // public final Spec XXX = new Spec("XXX", this);
    }
    public static class MonitorClass extends SVSpecGroup {
        public MonitorClass(SVSpecGroup parent) {
            super("Monitor", parent);
        }
        // public final Spec XXX = new Spec("XXX", this);
    }

}
