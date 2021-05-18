package com.webapp.library.repositories;

import com.webapp.library.data.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, String> {

}
