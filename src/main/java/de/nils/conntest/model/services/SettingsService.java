package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.daos.Setting;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SettingsService implements EventListener
{
    private final Model model;

    public SettingsService(Model model)
    {
        this.model = model;
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case THEME_CHANGED ->
            {
                Setting setting = model.getSettingsRepo().get(Const.Settings.THEME_KEY);

                if(setting == null)
                {
                    setting = new Setting(Const.Settings.THEME_KEY);
                    setting.setValue(event.getData(Const.Event.SETTINGS_VALUE));
                    model.getSettingsRepo().create(setting);
                }
                else
                {
                    setting.setValue(event.getData(Const.Event.SETTINGS_VALUE));
                    model.getSettingsRepo().update(setting);
                }
            }
            case ENCODING_CHANGED ->
            {
                Setting setting = model.getSettingsRepo().get(Const.Settings.ENCODING_KEY);

                if(setting == null)
                {
                    setting = new Setting(Const.Settings.ENCODING_KEY);
                    setting.setValue(event.getData(Const.Event.SETTINGS_VALUE));
                    model.getSettingsRepo().create(setting);
                }
                else
                {
                    setting.setValue(event.getData(Const.Event.SETTINGS_VALUE));
                    model.getSettingsRepo().update(setting);
                }
            }
        }
    }
}
