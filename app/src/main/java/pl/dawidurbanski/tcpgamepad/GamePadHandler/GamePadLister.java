package pl.dawidurbanski.tcpgamepad.GamePadHandler;

import android.util.Log;
import android.view.InputDevice;

import java.util.ArrayList;

/**
 * Created by dawid on 30.01.2016.
 */
public class GamePadLister {

    private static ArrayList<InputDevice> devices = new ArrayList<>();

    public ArrayList<InputDevice> getGameControllers()
    {
        devices = new ArrayList<>();

        for (int deviceId : InputDevice.getDeviceIds()) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if ( ((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    ||
                    ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)
                    )
            {
                // This device is a game controller. Store its device ID.
                devices.add(dev);
                Log.w(GamePadLister.class.getName(), "found gamePad: " + dev.getName());
            }
        }
        return devices;
    }
}
