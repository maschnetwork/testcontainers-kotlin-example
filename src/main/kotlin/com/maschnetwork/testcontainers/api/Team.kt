package com.maschnetwork.testcontainers.api

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*


@RestController
class TeamController(private val teamService: TeamService) {


    @GetMapping("/api/teams/{name}")
    fun getTeams(@PathVariable name : String) : ResponseEntity<Team> {
        return ResponseEntity.ok(teamService.getTeam(name))
    }

    @PostMapping("/api/teams")
    fun createTeam(@RequestBody body: Map<String, String>) : ResponseEntity<Team> {
        val createdTeam = teamService.createTeam(body["name"] ?: throw IllegalArgumentException("Name not provided"))
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam)
    }

}

interface TeamService {
    fun getTeam(name: String) : Team
    fun createTeam(name: String): Team
}

@Service
class TeamServiceImpl: TeamService {

    private val teams = mutableListOf<Team>()

    @Cacheable(cacheNames = ["team"], key = "#name")
    override fun getTeam(name : String) : Team {
        return teams.find { it.name == name } ?: throw IllegalArgumentException("$name not present")
    }

    override fun createTeam(name : String) : Team {
        val team = Team(name)
        teams.add(team)
        return team
    }
}


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class Team(val name: String)