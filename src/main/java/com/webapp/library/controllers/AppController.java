package com.webapp.library.controllers;

import com.webapp.library.data.WebUser;
import com.webapp.library.repositories.BookRepository;
import com.webapp.library.repositories.WebUserRepository;
import com.webapp.library.session.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Service
public class AppController {

    private final WebUserRepository webUserRepository;

    private final BookRepository bookRepository;

    public AppController(WebUserRepository webUserRepository, BookRepository bookRepository) {
        this.webUserRepository = webUserRepository;
        this.bookRepository = bookRepository;
    }

    @RequestMapping("/index")
    public String start(){
        if(isAuthenticated())
            return "redirect:/books";
        return "index";
    }

    @RequestMapping("/")
    public String start1(){
        if(isAuthenticated())
            return "redirect:/books";
        return "index";
    }

    @RequestMapping("/registration")
    public String registrationForm(Model model){
        if(isAuthenticated())
            return "redirect:/books";

        WebUser webUser = new WebUser();
        model.addAttribute("webUser", webUser);

        return "registration";
    }

    @PostMapping("/register")
    @Transactional
    public String register(Model model, @ModelAttribute WebUser wu){

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(wu.getRawPassword());
        wu.setPassword(encodedPassword);

        webUserRepository.save(wu);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        if(isAuthenticated())
            return "redirect:/books";
        model.addAttribute("webUser", new WebUser());

        model.addAttribute("users", webUserRepository.findAll());

        return "login";
    }

    @GetMapping("/books")
    public String booksForm(Model model) {

        model.addAttribute("uid", getCurrentUserId());
        model.addAttribute("books", bookRepository.findAll());
        return "books";
    }

    @GetMapping("/free_books")
    public String freeBooksForm(Model model) {

        model.addAttribute("uid", getCurrentUserId());
        model.addAttribute("books", bookRepository.findAll());
        return "free_books";
    }

    @PostMapping("/borrow_book")
    @Transactional
    public String borrowBook(Model model, @RequestParam String bookId){

        bookRepository.findById(bookId).get().setWebUser(webUserRepository.findByUsername(getCurrentUserUsername()));

        return "redirect:/books";
    }

    @PostMapping("/return_book")
    @Transactional
    public String returnBook(Model model, @RequestParam String bookId){

        bookRepository.findById(bookId).get().setWebUser(null);

        return "redirect:/books";
    }

    private Long getCurrentUserId(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Long id = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                id = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
            } else if (authentication.getPrincipal() instanceof String) {
                id = (Long) authentication.getPrincipal();
            }
        }
        return id;
    }

    private String getCurrentUserUsername (){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String usrName = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                usrName = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                usrName = (String) authentication.getPrincipal();
            }
        }
        return usrName;
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

}
