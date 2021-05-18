package com.webapp.library.boot;

import com.webapp.library.data.Book;
import com.webapp.library.data.WebUser;
import com.webapp.library.repositories.BookRepository;
import com.webapp.library.repositories.WebUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@Component
public class BootData implements CommandLineRunner {

    private final WebUserRepository webUserRepository;
    private final BookRepository bookRepository;

    public BootData(WebUserRepository webUserRepository, BookRepository bookRepository) {
        this.webUserRepository = webUserRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("START");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        WebUser defaultUser = new WebUser("user", passwordEncoder.encode("user"), "user", "User", "Default");
        webUserRepository.save(defaultUser);
        WebUser defaultUser1 = new WebUser("jane", passwordEncoder.encode("123"), "123", "Jane", "Doe");
        webUserRepository.save(defaultUser1);
        WebUser defaultUser2 = new WebUser("john", passwordEncoder.encode("456"), "456", "John", "Smith");
        webUserRepository.save(defaultUser2);


        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        InputStream in = new FileInputStream("src/main/resources/books.xml");
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

        Book b = null;

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                String elementName = startElement.getName().getLocalPart();
                switch (elementName) {
                    case "book":
                        b = new Book();

                        Iterator<Attribute> attributes = startElement.getAttributes();
                        while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            if (attribute.getName().toString().equals("id")) {
                                b.setId(attribute.getValue());
                            }
                        }
                        break;
                    case "author":
                        event = eventReader.nextEvent();
                        b.setAuthor(event.asCharacters().getData());
                        break;
                    case "title":
                        event = eventReader.nextEvent();
                        b.setTitle(event.asCharacters().getData());
                        break;
                    case "genre":
                        event = eventReader.nextEvent();
                        b.setGenre(event.asCharacters().getData());
                        break;
                    case "price":
                        event = eventReader.nextEvent();
                        b.setPrice(Float.parseFloat(event.asCharacters().getData()));
                        break;
                    case "publish_date":
                        event = eventReader.nextEvent();
                        String readDate=event.asCharacters().getData();
                        Date date=new SimpleDateFormat("yyyy-MM-dd").parse(readDate);

                        b.setPublishDate(date);

                        break;
                    case "description":
                        event = eventReader.nextEvent();
                        b.setDescription(event.asCharacters().getData());
                        break;
                }
            }

            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("book")) {
                    bookRepository.save(b);
                }
            }

        }
        System.out.println("Loaded books: "+bookRepository.count());
    }
}
