package mcp.mobius.waila.gui.screen;

import java.util.List;

import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.util.DisplayUtil.createButton;

public abstract class ConfigScreen extends Screen {

    private final Screen parent;
    private final Runnable saver;
    private final Runnable canceller;

    @SuppressWarnings("unchecked")
    private final List<GuiEventListener> children = (List<GuiEventListener>) children();
    private ConfigListWidget options;

    protected boolean cancelled;

    public ConfigScreen(Screen parent, Component title, Runnable saver, Runnable canceller) {
        super(title);

        this.parent = parent;
        this.saver = saver;
        this.canceller = canceller;
    }

    public ConfigScreen(Screen parent, Component title) {
        this(parent, title, null, null);
    }

    @Override
    public void init() {
        super.init();

        if (options == null) {
            options = getOptions();
        }

        var searchBox = options.getSearchBox();
        if (searchBox.isActive()) addWidget(searchBox);

        addWidget(options);
        options.init();

        if (saver != null && canceller != null) {
            addRenderableWidget(createButton(width / 2 - 102, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> {
                if (options.save(false)) {
                    saver.run();
                    onClose();
                }
            }));
            addRenderableWidget(createButton(width / 2 + 2, height - 25, 100, 20, CommonComponents.GUI_CANCEL, w -> {
                cancelled = true;
                canceller.run();
                onClose();
            }));
        } else {
            addRenderableWidget(createButton(width / 2 - 50, height - 25, 100, 20, CommonComponents.GUI_DONE, w -> {
                if (options.save(false)) {
                    onClose();
                }
            }));
        }

        if (searchBox.isActive()) setInitialFocus(searchBox);
    }

    protected void renderForeground(GuiGraphics ctx, int rowLeft, int rowWidth, int mouseX, int mouseY, float partialTicks) {
        ctx.drawString(font, title, rowLeft, 12, 0xFFFFFF);
    }

    @Override
    public void tick() {
        options.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        super.render(ctx, mouseX, mouseY, partialTicks);

        options.render(ctx, mouseX, mouseY, partialTicks);

        var searchBox = options.getSearchBox();
        if (searchBox.isActive()) options.getSearchBox().render(ctx, mouseX, mouseY, partialTicks);

        renderForeground(ctx, options.getRowLeft(), options.getRowWidth(), mouseX, mouseY, partialTicks);

        if (mouseY < 32 || mouseY > height - 32) {
            return;
        }

        options.getChildAt(mouseX, mouseY).ifPresent(element -> {
            if (element instanceof ConfigValue<?> value) {
                value.renderTooltip(ctx, mouseX, mouseY);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var child : children) {
            if (child instanceof EditBox editBox) {
                editBox.setFocused(false);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public void addListener(GuiEventListener listener) {
        children.add(listener);
    }

    public abstract ConfigListWidget getOptions();

}
