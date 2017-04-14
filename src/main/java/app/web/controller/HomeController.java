package app.web.controller;

import app.web.dto.Book;
import app.web.service.BooksService;
import app.web.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Gabriel on 02.02.2017.
 */
@Controller
@RequestMapping({"/", "/home"})
public class HomeController {

    private CategoriesService categoriesService;
    private BooksService booksService;

    @Autowired
    HomeController(CategoriesService categoriesService, BooksService booksService) {
        this.categoriesService = categoriesService;
        this.booksService = booksService;
    }

    @RequestMapping(method = GET)
    public String home(Model model) {
        Map<String, String> myParam = categoriesService.makeBooksCategories();
        model.addAttribute("categories", myParam);

        List<Book> books = booksService.retrieveBooks("Komiks", 15, 0);
        model.addAttribute("books", books);

        return "home";
    }
}
