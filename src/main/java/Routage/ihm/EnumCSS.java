package Routage.ihm;

public enum EnumCSS
{
    STYLE_PC("fill-color: black;text-background-mode: plain; text-background-color: white;text-alignment: under;" +
            "text-size: 15; shape:box;stroke-mode:plain;stroke-color:red;"),
    STYLE_ROUTEUR("fill-color: black; text-background-mode: plain; text-background-color: white;text-alignment: under;" +
            "text-size: 15; shape:circle;stroke-mode:plain;stroke-color:yellow;"),
    STYLE_EDGE("fill-color: black; text-background-mode: plain; text-background-color: white;text-size: 15;");

    String s;

    EnumCSS(String s)
    {
        this.s = s;
    }

    public String getS()
    {
        return s;
    }
}
