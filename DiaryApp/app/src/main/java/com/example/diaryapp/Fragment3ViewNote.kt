/**
 * Student ID: 2940038
 */

package com.example.diaryapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diaryapp.databinding.Fragment3ViewNoteBinding
import java.util.Calendar

class Fragment3ViewNote : Fragment() {

    // uses view binding technique to bind the layout to the fragment and use the elements from the UI easily
    private var _binding: Fragment3ViewNoteBinding? = null
    private val binding get() = _binding!!

    // initializes the databaseAdapter
    private lateinit var databaseAdapter: DatabaseAdapter

    // the date of the note selected by the user is saved here
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment3ViewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // creates an instance of the databaseAdapter
        databaseAdapter = DatabaseAdapter(requireContext())

        // open the database connection
        databaseAdapter.open()

        // display the notes in the list view
        displayAllNotes()

        // handles the add note button
        binding.addNoteButton.setOnClickListener {
            (activity as? MainActivity)?.moveToFragment1()
        }

        // handles the filter view button
        binding.filterViewButton.setOnClickListener {
            displayDatePicker()
        }

        // handles the update note button
        binding.updateNoteButton.setOnClickListener {
            // checks if the note is selected or not
            if (selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Select a note first. Click on a note to select it.", Toast.LENGTH_LONG).show()
            } else {
                // calls the updateMote() method which uses getNoteByDate() method from the databaseAdapter to get the note by date
                updateNote(databaseAdapter.getNoteByDate(selectedDate))
            }
        }

        // handles the delete note button
        binding.deleteNoteButton.setOnClickListener {
            // checks if the note is selected or not
            if (selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Select a note first. Click on a note to select it.", Toast.LENGTH_LONG).show()
            }
            else {
                deleteNote(selectedDate)
            }
        }

        // Handle deleteAllButton click
        binding.deleteAllButton.setOnClickListener {
            deleteAllNotes()
        }
    }

    /**
     * Display all notes using getAllNotes() method from databaseAdapter
     */
    private fun displayAllNotes() {
        val notesCursor = databaseAdapter.getAllNotes()
        viewNotesInList(notesCursor)
    }

    /**
     * View the notes in the list view
     * @param cursor, the cursor contains data of all the notes that it retrieves from the database by using getAllNotes() method in displayAllNotes() method
     */
    private fun viewNotesInList(cursor: Cursor) {
        val notesList = mutableListOf<String>()

        // check if the cursor has valid data and move to the first row
        if (cursor.moveToFirst()) {
            do {
                // check if the column index is valid
                val dateColumnIndex = cursor.getColumnIndex(DatabaseAdapter.KEY_DATE)
                val noteColumnIndex = cursor.getColumnIndex(DatabaseAdapter.KEY_NOTE)

                if (dateColumnIndex != -1 && noteColumnIndex != -1) {
                    // retrieve data from columns if the column index is valid
                    val date = cursor.getString(dateColumnIndex)
                    val note = cursor.getString(noteColumnIndex)

                    // add data to the list
                    notesList.add("Date: $date\nNote: $note")
                }
            } while (cursor.moveToNext())
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notesList)
        binding.listView.adapter = adapter

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            selectedDate = getDateFromPosition(position, cursor)
            Toast.makeText(requireContext(), "Note has been selected", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Gets the date of the note from the position, when a user clicks on the note in list view it gets the date of that note
     * @param position, The index of the item in the cursor from which the date needs to be retrieved
     * @param cursor, the cursor that contains the data
     * @return the date of the note
     */
    private fun getDateFromPosition(position: Int, cursor: Cursor): String {
        cursor.moveToPosition(position)
        val dateColumnIndex = cursor.getColumnIndex(DatabaseAdapter.KEY_DATE)
        return if (dateColumnIndex != -1) {
            cursor.getString(dateColumnIndex)
        } else {
            ""
        }
    }

    /**
     * Displays the date picker dialog box
     */
    private fun displayDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // gets the date and month formatted to MM and DD properly
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                // gets the selected date
                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                displayFilteredNotes(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    /**
     * Display filtered note by date
     * The date is selected using the displayDatePicker() method
     * @param selectedDate, the date of the note to be shown
     */
    private fun displayFilteredNotes(selectedDate: String) {
        val filteredNotesCursor = databaseAdapter.filterNotesByDate(selectedDate)

        if (filteredNotesCursor.moveToFirst()) {
            // retrieve data from columns
            val dateIndex = filteredNotesCursor.getColumnIndex(DatabaseAdapter.KEY_DATE)
            val noteIndex = filteredNotesCursor.getColumnIndex(DatabaseAdapter.KEY_NOTE)

            if (dateIndex != -1 && noteIndex != -1) {
                // retrieve data from the cursor
                val date = filteredNotesCursor.getString(dateIndex)
                val note = filteredNotesCursor.getString(noteIndex)

                // display the note in a dialog box
                AlertDialog.Builder(requireContext())
                    .setTitle("Note Details")
                    .setMessage("Date: $date\nNote: $note")
                    .setPositiveButton("OK", null)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "No notes found for the selected date", Toast.LENGTH_SHORT).show()
        }

        // close the cursor
        filteredNotesCursor.close()
    }

    /**
     * Update a note by date
     * When a note is clicked in the list view the getDateFromPosition() method is used
     * which gets the date of the note which needs to be updated
     * @param note, the previous note that needs to be displayed in the edit text so the user can update the edit text
     */
    private fun updateNote(note: String) {
        val editText = EditText(requireContext())
        // sets the text in edit text by getting the old note which needs to be updated in it
        editText.setText(note)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Note")
            .setMessage(selectedDate)
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val updatedNote = editText.text.toString()

                // update the note
                val success = databaseAdapter.updateNoteByDate(selectedDate, updatedNote)

                if (success) {
                    Toast.makeText(requireContext(), "Note updated", Toast.LENGTH_SHORT).show()

                    // refresh the list view
                    displayAllNotes()
                } else {
                    Toast.makeText(requireContext(), "Failed to update note", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Delete a note by date
     * When a note is clicked in the list view the getDateFromPosition() method is used
     * which gets the date of the note which needs to be deleted
     * @param selectedDate, the date of the note that needs to be deleted
     */
    private fun deleteNote(selectedDate: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete the note for $selectedDate?")
            .setPositiveButton("Yes") { _, _ ->
                // delete the note by date
                val success = databaseAdapter.deleteNoteByDate(selectedDate)
                if (success) {
                    Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
                    // refresh the list view
                    displayAllNotes()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete note", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    /**
     * Delete all notes and refresh the list view
     */
    private fun deleteAllNotes() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All Notes")
            .setMessage("Are you sure you want to delete all notes?")
            .setPositiveButton("Yes") { _, _ ->
                // delete all notes
                databaseAdapter.deleteAllNotes()
                // refresh the list view
                displayAllNotes()
                Toast.makeText(requireContext(), "All notes deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // close the database connection when the fragment is destroyed
        databaseAdapter.close()
    }
}
