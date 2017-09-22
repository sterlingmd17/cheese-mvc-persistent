package org.launchcode.controllers;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model){
        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model){
        Menu menu = new Menu();
        model.addAttribute("title","Add Menu");
        model.addAttribute("menu", menu);
        return "menu/add";

    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add (Model model,
                       @ModelAttribute @Valid Menu menu,
                       Errors errors){
        if (errors.hasErrors()){
            model.addAttribute("title","Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/"+ menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id){
        Menu menu=menuDao.findOne(id);
        model.addAttribute("menu",menu);
        model.addAttribute("title", menu.getName());
        return "menu/view";
    }

    @RequestMapping(value ="add-item/{id}", method = RequestMethod.GET)
    public String addItem (Model model, @PathVariable int id){
        Menu menu = menuDao.findOne(id);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: "+menu.getName());
        return "menu/add-item";

    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String addItem(Model model,
                          @ModelAttribute @Valid  AddMenuItemForm form,
                          @PathVariable int id,
                          Errors errors){
        if (errors.hasErrors()){
            model.addAttribute("form",form);
            return "menu/add-item";
        }
        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(id);
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);
        return "redirect:/menu/view/"+theMenu.getId();
    }

}
