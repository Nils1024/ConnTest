package de.nils.conntest.model.repo;

import de.nils.conntest.model.daos.Setting;

public class SettingsRepo extends RepoBase<String, Setting>
{
    public SettingsRepo()
    {
        super("Settings");
    }

    @Override
    public String extractKey(Setting dao)
    {
        return dao.getKey();
    }
}
