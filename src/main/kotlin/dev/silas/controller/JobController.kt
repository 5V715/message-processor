package dev.silas.controller

import dev.silas.model.Job
import dev.silas.repository.MessageRepository
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JobController(
    private val repository: MessageRepository
) {
    @GetMapping("/all")
    fun getAll() = repository.getAll()

    @PostMapping("/job")
    fun newJob(@RequestBody @Validated job: Job): ResponseEntity<Job> {
        repository.addNew(job)
        return ResponseEntity.accepted().body(job)
    }
}
