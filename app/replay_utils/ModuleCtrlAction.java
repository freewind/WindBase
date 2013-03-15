package replay_utils;

public class ModuleCtrlAction {

    public String module;
    public String controller;
    public String action;

    public ModuleCtrlAction(String module, String controller, String action) {
        this.module = module;
        this.controller = controller;
        this.action = action;
    }

    public String getPath() {
        return (module == null ? "" : "/" + module) + "/" + controller + "/" + action;
    }

    public String getLink() {
        return (module == null ? "" : module + ".") + controller + "." + action;
    }

    @Override
    public String toString() {
        return getLink();
    }

}