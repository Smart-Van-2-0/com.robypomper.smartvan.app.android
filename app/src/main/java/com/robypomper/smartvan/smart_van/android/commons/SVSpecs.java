package com.robypomper.smartvan.smart_van.android.commons;

import java.util.List;

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
        public final SVSpec Binary_1_N = new SVSpec("Binary 1-N", this);
        public final SVSpec Percentage_1_N = new SVSpec("Percentage 1-N", this);
    }
    public static class ActuatorsClass extends SVSpecGroup {
        public ActuatorsClass(SVSpecGroup parent) {
            super("Actuators", parent);
        }
        public final SVSpec Switch_Low_1_N = new SVSpec("Switch Low 1-N", this);
        public final SVSpec Switch_Low_Avg_Current = new SVSpec("Switch Low Avg Current", this);
        public final SVSpec Switch_Low_Max_Current = new SVSpec("Switch Low Max Current", this);
        public final SVSpec Switch_High_1_N = new SVSpec("Switch High 1-N", this);
        public final SVSpec Switch_High_Avg_Current = new SVSpec("Switch High Avg Current", this);
        public final SVSpec Dimmer_Low_1_N = new SVSpec("Dimmer Low 1-N", this);
        public final SVSpec Dimmer_Low_Avg_Current = new SVSpec("Dimmer Low Avg Current", this);
        public final SVSpec Dimmer_Low_Max_Current = new SVSpec("Dimmer Low Max Current", this);
        public final SVSpec Dimmer_High_1_N = new SVSpec("Dimmer High 1-N", this);
        public final SVSpec Dimmer_High_Avg_Current = new SVSpec("Dimmer High Avg Current", this);
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
