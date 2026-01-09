package de.nils.conntest.model.daos;

public class Setting
{
    private final String key;
    private String value;

    public Setting(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
