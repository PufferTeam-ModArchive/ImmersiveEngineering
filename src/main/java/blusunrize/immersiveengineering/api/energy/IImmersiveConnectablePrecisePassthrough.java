package blusunrize.immersiveengineering.api.energy;

/**
 * @author unfoidable - 17.05.2019
 *
 *         An interface to be implemented by TileEntities, to allow precise energy passthrough monitoring
 */
public interface IImmersiveConnectablePrecisePassthrough {

    /**
     * fired for every not-simulated energy packet passing through. Used for energy meter and stuff
     */
    public void onEnergyPassthrough(double amount);
}
